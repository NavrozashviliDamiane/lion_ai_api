package levani.ai.app.lionapp.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import levani.ai.app.lionapp.domain.Base;
import levani.ai.app.lionapp.dto.ChatRequest;
import levani.ai.app.lionapp.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiQueryService {

    private final ChatClient.Builder chatClientBuilder;
    private final InMemoryDataCache inMemoryDataCache;
    private final EmbeddingService embeddingService;

    private static final String QUERY_GENERATION_PROMPT = """
            თქვენ ხართ SQL მოთხოვნების გენერატორი MariaDB მონაცემთა ბაზისთვის.
            You are a SQL query generator for a MariaDB database.
            
            მონაცემთა ბაზის სქემა / Database Schema:
            ცხრილი / Table: vps_liontest_db.base
            
            ძირითადი სვეტები / Key Columns:
            - id (INT): პირველადი გასაღები / Primary key
            - author (INT): ავტორის ID / Author ID
            - date (DATE): ჩანაწერის თარიღი / Record date
            - buyer_id (VARCHAR): მყიდველის იდენტიფიკატორი / Buyer identifier
            - manufacturer (INT): მწარმოებლის ID / Manufacturer ID
            - model (INT): მოდელის ID / Model ID
            - year (VARCHAR): წელი / Year
            - vin (VARCHAR): VIN ნომერი / Vehicle identification number
            - stock_lot (VARCHAR): სტოკის/ლოტის ნომერი / Stock/Lot number
            - auction_pay (DOUBLE): აუქციონის გადახდის თანხა / Auction payment amount
            - deposit (DOUBLE): დეპოზიტი / Deposit
            - auction_real_pay (DOUBLE): აუქციონის რეალური გადახდა / Actual auction payment
            - auction_real_pay_date (DATE): აუქციონის რეალური გადახდის თარიღი / Actual payment date
            - dispatch_status (ENUM): სტატუსი - 'New', 'On Site', 'Order Taken', 'Pickup', 'Deliver'
            - container_number (VARCHAR): კონტეინერის ნომერი / Container number
            - container_status (TEXT): კონტეინერის სტატუსი / Container status
            - parts (TEXT): ნაწილების აღწერა / Parts description
            - branch (VARCHAR): ფილიალის სახელი / Branch name
            - pick_up_location (VARCHAR): აღების ადგილმდებარეობა / Pickup location
            - pick_up_date (DATE): აღების თარიღი / Pickup date
            - expect_delivery (DATE): მოსალოდნელი მიწოდება / Expected delivery
            - deliver_date (DATE): მიწოდების თარიღი / Delivery date
            - state (INT): შტატი / State
            - zip_number (VARCHAR): ZIP კოდი / ZIP code
            - carrier (VARCHAR): გადამზიდავი / Carrier
            - carrier_phone (VARCHAR): გადამზიდავის ტელეფონი / Carrier phone
            - personal_manager (INT): პერსონალური მენეჯერი / Personal manager
            - bank_account_name (INT): საბანკო ანგარიშის სახელი / Bank account name
            - receiver_company (INT): მიმღები კომპანია / Receiver company
            - agent_company (INT): აგენტის კომპანია / Agent company
            - name_surname (VARCHAR): სახელი გვარი / Name surname
            - passport_number (VARCHAR): პასპორტის ნომერი / Passport number
            - telephone (VARCHAR): ტელეფონი / Telephone
            - usa_geo (BOOLEAN): აშშ გეო / USA geo
            - warehouse (BOOLEAN): საწყობი / Warehouse
            - canceling (BOOLEAN): გაუქმება / Canceling
            - ready_for_dispatch (BOOLEAN): მზადაა გაგზავნისთვის / Ready for dispatch
            
            მომხმარებლის მოთხოვნა (შეიძლება იყოს ქართულად ან ინგლისურად):
            User Query (can be in Georgian or English): {query}
            {authorContext}
            
            გენერირება მხოლოდ ვალიდური SQL SELECT მოთხოვნა MariaDB-სთვის.
            Generate ONLY a valid SQL SELECT query for MariaDB.
            
            წესები / Rules:
            1. ყოველთვის გამოიყენეთ სრული ცხრილის სახელი: vps_liontest_db.base
            2. დააბრუნეთ მხოლოდ SQL მოთხოვნა, განმარტებების გარეშე
            3. გამოიყენეთ სწორი SQL სინტაქსი MariaDB-სთვის
            4. შეიტანეთ ORDER BY date DESC როდესაც შესაფერისია
            5. შეზღუდეთ შედეგები მაქსიმუმ 100 სტრიქონით
            6. თუ ავტორის ID მითითებულია, ჩართეთ WHERE author = {authorId}
            7. ქართული ენის მოთხოვნები: გაიგეთ განზრახვა და დაუკავშირეთ სწორ სვეტების სახელებს
            8. მონაცემთა ბაზა იყენებს UTF-8 კოდირებას, ქართული ტექსტი სრულად მხარდაჭერილია
            
            ქართული მოთხოვნების მაგალითები / Georgian Query Examples:
            - "მაჩვენე ყველა ჩანაწერი ავტორი 2212-სთვის" → WHERE author = 2212
            - "იპოვე მანქანები 2020 წლიდან 2023 წლამდე" → WHERE year BETWEEN '2020' AND '2023'
            - "ჩანაწერები დიდი აუქციონის გადახდით" → ORDER BY auction_pay DESC
            - "რა არის პიკაპის სტატუსით" → WHERE dispatch_status = 'Pickup'
            - "კონტეინერის ნომერი შეიცავს ABC" → WHERE container_number LIKE '%ABC%'
            - "გასული თვის ჩანაწერები" → WHERE date >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
            
            SQL Query:
            """;

    private static final String RESPONSE_GENERATION_PROMPT = """
            თქვენ ხართ დამხმარე ასისტენტი, რომელიც ხსნის მონაცემთა ბაზის მოთხოვნის შედეგებს.
            You are a helpful assistant explaining database query results.
            
            ᲛᲜᲘᲨᲕᲜᲔᲚᲝᲕᲐᲜᲘ: უპასუხეთ იმავე ენაზე, რომელზეც მომხმარებელმა დასვა კითხვა!
            IMPORTANT: You MUST respond in the SAME language as the user's query!
            
            მომხმარებლის კითხვა / User asked: {query}
            
            ნაპოვნი ჩანაწერების რაოდენობა / Records found: {resultCount}
            
            მიაწოდეთ მოკლე, ბუნებრივი ენის რეზიუმე იმის შესახებ, რაც იპოვა.
            Provide a concise, natural language summary of what was found.
            
            ფოკუსირება მოთხოვნის ძირითად ინფორმაციაზე.
            Focus on the key information from the query.
            
            ენის განსაზღვრის წესები / Language Detection Rules:
            - თუ მომხმარებელმა ქართულად იკითხა → უპასუხეთ ქართულად
            - If user asked in Georgian → respond in Georgian
            - თუ მომხმარებელმა ინგლისურად იკითხა → უპასუხეთ ინგლისურად
            - If user asked in English → respond in English
            
            ქართული პასუხის მაგალითები / Georgian Response Examples:
            - "ნაპოვნია 15 ჩანაწერი ავტორი 2212-სთვის"
            - "მოიძებნა 8 მანქანა 2020-2023 წლების დიაპაზონში"
            - "აღმოჩნდა 23 ჩანაწერი პიკაპის სტატუსით"
            
            პასუხი / Response:
            """;

    @Cacheable(value = "queryResults", key = "#request.query + '-' + #request.authorId")
    public ChatResponse processQuery(ChatRequest request) {
        try {
            String sqlQuery = generateSqlQuery(request);
            log.info("Generated SQL: {}", sqlQuery);

            List<Base> results = executeQuery(sqlQuery);
            log.info("Query returned {} results", results.size());

            String naturalResponse = generateNaturalResponse(request.getQuery(), results.size());

            return ChatResponse.success(naturalResponse, sqlQuery, results);

        } catch (Exception e) {
            log.error("Error processing query: {}", e.getMessage(), e);
            return ChatResponse.error("Failed to process query: " + e.getMessage());
        }
    }

    public ChatResponse processQueryWithContext(ChatRequest request, String conversationContext) {
        try {
            String sqlQuery = generateSqlQuery(request);
            log.info("Generated SQL: {}", sqlQuery);

            List<Base> results = executeQuery(sqlQuery);
            log.info("Query returned {} results", results.size());

            String naturalResponse = generateConversationalResponse(request.getQuery(), results, conversationContext);

            return ChatResponse.success(naturalResponse, sqlQuery, results);

        } catch (Exception e) {
            log.error("Error processing query with context: {}", e.getMessage(), e);
            return ChatResponse.error("Failed to process query: " + e.getMessage());
        }
    }

    private String generateSqlQuery(ChatRequest request) {
        String authorContext = request.getAuthorId() != null 
            ? "Author ID filter: " + request.getAuthorId() 
            : "No author filter specified";

        Map<String, Object> params = Map.of(
            "query", request.getQuery(),
            "authorContext", authorContext,
            "authorId", request.getAuthorId() != null ? request.getAuthorId() : ""
        );

        PromptTemplate promptTemplate = new PromptTemplate(QUERY_GENERATION_PROMPT);
        Prompt prompt = promptTemplate.create(params);

        ChatClient chatClient = chatClientBuilder.build();
        String response = chatClient.prompt(prompt)
                .call()
                .content();

        return cleanSqlQuery(response);
    }

    private String cleanSqlQuery(String sqlQuery) {
        sqlQuery = sqlQuery.trim();
        
        if (sqlQuery.startsWith("```sql")) {
            sqlQuery = sqlQuery.substring(6);
        } else if (sqlQuery.startsWith("```")) {
            sqlQuery = sqlQuery.substring(3);
        }
        
        if (sqlQuery.endsWith("```")) {
            sqlQuery = sqlQuery.substring(0, sqlQuery.length() - 3);
        }
        
        sqlQuery = sqlQuery.trim();
        
        if (!sqlQuery.toUpperCase().startsWith("SELECT")) {
            throw new IllegalArgumentException("Generated query must be a SELECT statement");
        }
        
        return sqlQuery;
    }

    private List<Base> executeQuery(String sqlQuery) {
        try {
            log.debug("Executing query against in-memory cache: {}", sqlQuery);
            
            String upperQuery = sqlQuery.toUpperCase();
            List<Base> results = new java.util.ArrayList<>();
            
            if (upperQuery.contains("WHERE AUTHOR")) {
                int authorId = extractIntValue(sqlQuery, "AUTHOR");
                results = inMemoryDataCache.filterByAuthor(authorId);
            } else if (upperQuery.contains("WHERE YEAR")) {
                String year = extractStringValue(sqlQuery, "YEAR");
                results = inMemoryDataCache.filterByYear(year);
            } else if (upperQuery.contains("WHERE DISPATCH_STATUS")) {
                String status = extractStringValue(sqlQuery, "DISPATCH_STATUS");
                results = inMemoryDataCache.filterByDispatchStatus(status);
            } else if (upperQuery.contains("WHERE VIN")) {
                String vin = extractStringValue(sqlQuery, "VIN");
                results = inMemoryDataCache.searchRecords(vin);
            } else if (upperQuery.contains("WHERE CONTAINER_NUMBER")) {
                String container = extractStringValue(sqlQuery, "CONTAINER_NUMBER");
                results = inMemoryDataCache.searchRecords(container);
            } else if (upperQuery.contains("WHERE MANUFACTURER")) {
                String manufacturer = extractStringValue(sqlQuery, "MANUFACTURER");
                results = inMemoryDataCache.filterByManufacturer(manufacturer);
            } else if (upperQuery.contains("WHERE MODEL")) {
                String model = extractStringValue(sqlQuery, "MODEL");
                results = inMemoryDataCache.filterByModel(model);
            } else if (upperQuery.contains("WHERE AUCTION_PAY")) {
                results = inMemoryDataCache.getAllRecords();
            } else {
                results = inMemoryDataCache.getAllRecords();
            }
            
            if (upperQuery.contains("ORDER BY DATE DESC")) {
                results.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            } else if (upperQuery.contains("ORDER BY YEAR DESC")) {
                results.sort((a, b) -> {
                    String yearA = a.getYear() != null ? a.getYear() : "0";
                    String yearB = b.getYear() != null ? b.getYear() : "0";
                    return yearB.compareTo(yearA);
                });
            }
            
            if (upperQuery.contains("LIMIT")) {
                int limit = extractLimit(sqlQuery);
                results = results.stream().limit(limit).toList();
            }
            
            return results;
            
        } catch (Exception e) {
            log.error("Error executing query against cache: {}", e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }

    private int extractIntValue(String query, String fieldName) {
        try {
            String pattern = fieldName + "\\s*=\\s*(\\d+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(query.toUpperCase());
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        } catch (Exception e) {
            log.debug("Could not extract int value for {}: {}", fieldName, e.getMessage());
        }
        return 0;
    }

    private String extractStringValue(String query, String fieldName) {
        try {
            String pattern = fieldName + "\\s*=\\s*['\"]([^'\"]+)['\"]";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher m = p.matcher(query);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            log.debug("Could not extract string value for {}: {}", fieldName, e.getMessage());
        }
        return "";
    }

    private int extractLimit(String query) {
        try {
            String pattern = "LIMIT\\s+(\\d+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher m = p.matcher(query);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        } catch (Exception e) {
            log.debug("Could not extract limit: {}", e.getMessage());
        }
        return 100;
    }

    private String generateNaturalResponse(String userQuery, int resultCount) {
        Map<String, Object> params = Map.of(
            "query", userQuery,
            "resultCount", resultCount
        );

        PromptTemplate promptTemplate = new PromptTemplate(RESPONSE_GENERATION_PROMPT);
        Prompt prompt = promptTemplate.create(params);

        ChatClient chatClient = chatClientBuilder.build();
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    private String generateConversationalResponse(String userQuery, List<Base> results, String conversationContext) {
        // Build a rich context about the results for more engaging responses
        String resultContext = buildResultContext(results);
        
        String conversationalPrompt = """
            თქვენ ხართ მეგობრული, ჭკვიანი და დამხმარე AI ასისტენტი, რომელიც ხელმძღვანელობს Car Dealer Admin-ის მანქანების ინვენტარის მართვის სისტემას.
            You are a friendly, intelligent and helpful AI assistant that helps Car Dealer Admins manage their vehicle inventory and operations.
            
            ᲙᲝᲜᲢᲔᲥᲡᲢᲘ / CONTEXT:
            - ეს არის Car Dealer Admin-ის ადმინისტრაციული გვერდი, არა კლიენტის გვერდი / This is a Car Dealer Admin management page, not a client-facing page
            - მომხმარებელი არის მანქანების დილერის ადმინისტრატორი / The user is a car dealer administrator
            - პასუხები უნდა იყოს ბიზნეს-ორიენტირებული და პროფესიონალური / Responses should be business-oriented and professional
            - ფოკუსი უნდა იყოს ინვენტარის მართვაზე, გაყიდვებზე, სტატუსზე და ოპერაციულ მონაცემებზე / Focus on inventory management, sales, status, and operational data
            - დილერი იყენებს ამ სისტემას თავის მანქანების ტრეკირებისთვის / The dealer uses this system to track their vehicles
            
            ᲙᲠᲘᲢᲘᲙᲣᲚᲘ: უპასუხეთ იმავე ენაზე, რომელზეც მომხმარებელმა დასვა კითხვა!
            CRITICAL: You MUST respond in the SAME language as the user's query!
            
            ქართული ენის გრამატიკული სწორება:
            Georgian Grammar Rules:
            1. გამოიყენეთ სწორი ზმნის დროები - აორისტი, პრეზენტი, პერფექტი / Use correct verb tenses
            2. დაკითხვებზე პასუხი დაიწყეთ პირდაპირი პასუხით / Start with direct answer to questions
            3. გამოიყენეთ ბუნებრივი ქართული გამოთქმები, არა სიტყვასიტყვე თარგმანი / Use natural Georgian expressions, not literal translations
            4. სახელდებული სიტყვების სწორი ფორმა - ნომინატივი, ბატონობითი, მოქმედებითი / Use correct noun cases
            5. ზედსართავი სიტყვების სწორი შედარება / Use correct adjective forms
            6. მაქვს/მაქვთ/აქვს/აქვთ - სწორი ფორმა / Use correct possessive forms
            7. არის/არიან - სწორი რიცხვი / Use correct number agreement
            8. წინდებულების სწორი გამოყენება (ში, ზე, ის, თან, მიერ) / Use correct prepositions
            
            ოპერაციის ისტორია / Conversation History:
            {conversationContext}
            
            მომხმარებლის ახალი კითხვა / User's new question: {query}
            
            ხელმისაწვდომი მონაცემები / Available Data:
            {resultContext}
            
            მიაწოდეთ ბუნებრივი, კონვერსაციული პასუხი, რომელიც:
            Provide a natural, conversational response that:
            1. პირველი რიგში პასუხობს კითხვას / Directly answers the question first
            2. ხელმისაწვდომი ენით ხსნის მნიშვნელოვან დეტალებს / Explains key details in accessible language
            3. მეგობრული, ბუნებრივი და ენთუზიაზმური ტონი / Uses a friendly, natural and enthusiastic tone
            4. თავიდან აიცილებს ცხრილების და ნომრების გადაჭარბებას / Avoids excessive tables and numbers
            5. ხაზი გაუსვამს ყველაზე მნიშვნელოვან ინფორმაციას / Highlights the most important information
            6. თუ შესაბამისი, დაამატეთ ინტერესური დაკვირვებები / Add interesting observations if relevant
            7. გაითვალისწინებს წინა კითხვებს კონტექსტში / Consider previous questions in context
            8. იყავით ფლექსიბილური - პასუხი შეიძლება იყოს მხოლოდ ტექსტი ან ინფორმაცია / Be flexible - response can be text only or information
            9. თუ ბევრი შედეგი არის, აჯამეთ და მოიტანეთ ყველაზე მნიშვნელოვანი / If many results, summarize and highlight the most important
            
            ქართული პასუხის მაგალითები (გრამატიკულად სწორი, დილერ-ადმინის კონტექსტში) / Georgian Response Examples (Grammatically Correct, Dealer Admin Context):
            - "თქვენი ინვენტარიდან ყველაზე ახალი მანქანა არის 2025 წლის Hyundai Elantra, რომელიც 'New' სტატუსით არის. მისი აუქციონის ფასი იყო 3500 დოლარი."
            - "თქვენ გაქვთ 38 მანქანა 2023 წლის. მათ შორის ყველაზე ახალი 2025 წელი გამოშვებული მანქანაა, რომელიც 'Order Taken' სტატუსით არის."
            - "VIN ნომრით '3VVNX7B21PM308313' თქვენ გაქვთ Honda Civic, 2020 წელი გამოშვებული. ის ამჟამად 'New' სტატუსით არის და გამოსაგზავნი."
            - "თქვენი ინვენტარი შედგება 25 მანქანისგან. მათ შორის 12 არის 2021 წელი, 8 არის 2020 წელი, და 5 არის 2023 წელი."
            - "ყველაზე ძვირი მანქანა თქვენი ინვენტარიდან 5565 დოლარი ღირდა. ეს 2021 წელი გამოშვებული Toyota იყო, რომელიც ამჟამად 'Deliver' სტატუსით არის."
            - "თქვენი მანქანების საშუალო ფასი დაახლოებით 4000 დოლარია. ყველაზე მეტი მანქანა 2021 წელი გამოშვებული არის."
            
            მნიშვნელოვანი: პასუხი უნდა იყოს 1-3 წინადადება, ბუნებრივი, კასუალური და გრამატიკულად სწორი ქართული. არ დაწერო ფორმალური ან სტრუქტურირებული ტექსტი.
            IMPORTANT: Keep response to 1-3 sentences, natural, casual and grammatically correct Georgian. Don't write formal or structured text.
            
            პასუხი / Response:
            """;

        Map<String, Object> params = Map.of(
            "query", userQuery,
            "resultContext", resultContext,
            "conversationContext", conversationContext
        );

        PromptTemplate promptTemplate = new PromptTemplate(conversationalPrompt);
        Prompt prompt = promptTemplate.create(params);

        ChatClient chatClient = chatClientBuilder.build();
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    private String buildResultContext(List<Base> results) {
        if (results.isEmpty()) {
            return "No results found";
        }

        StringBuilder context = new StringBuilder();
        context.append(String.format("Found %d records:\n", results.size()));

        // Include all relevant data for AI to work with
        int limit = Math.min(5, results.size());
        for (int i = 0; i < limit; i++) {
            Base record = results.get(i);
            context.append(String.format(
                "Record %d: ID=%d, Year=%s, Manufacturer=%s, Model=%s, Price=$%.2f, Status=%s, VIN=%s, Date=%s\n",
                i + 1,
                record.getId(),
                record.getYear() != null ? record.getYear() : "N/A",
                record.getManufacturerName() != null ? record.getManufacturerName() : "Unknown",
                record.getModelName() != null ? record.getModelName() : "Unknown",
                record.getAuctionPay() != null ? record.getAuctionPay() : 0.0,
                record.getDispatchStatus() != null ? record.getDispatchStatus() : "N/A",
                record.getVin() != null ? record.getVin() : "N/A",
                record.getDate() != null ? record.getDate() : "N/A"
            ));
        }

        if (results.size() > 5) {
            context.append(String.format("... and %d more records\n", results.size() - 5));
        }

        return context.toString();
    }

    private String buildResultSummary(List<Base> results) {
        if (results.isEmpty()) {
            return "არ მოიძებნა შესაბამისი ჩანაწერი / No matching records found";
        }

        StringBuilder summary = new StringBuilder();
        summary.append(String.format("ჯამი: %d ჩანაწერი / Total: %d records\n", results.size(), results.size()));

        // Get first few records for context
        int limit = Math.min(3, results.size());
        for (int i = 0; i < limit; i++) {
            Base record = results.get(i);
            String manufacturerName = record.getManufacturerName() != null ? record.getManufacturerName() : "Unknown";
            String modelName = record.getModelName() != null ? record.getModelName() : "Unknown";
            Double auctionPay = record.getAuctionPay() != null ? record.getAuctionPay() : 0.0;
            String status = record.getDispatchStatus() != null ? record.getDispatchStatus() : "N/A";
            
            summary.append(String.format(
                "- ID: %d, წელი: %s, მწარმოებელი: %s, მოდელი: %s, ფასი: $%.2f, სტატუსი: %s\n",
                record.getId(),
                record.getYear(),
                manufacturerName,
                modelName,
                auctionPay,
                status
            ));
        }

        if (results.size() > 3) {
            summary.append(String.format("... და %d სხვა ჩანაწერი / ... and %d more records\n", results.size() - 3));
        }

        return summary.toString();
    }

    public ChatResponse searchWithEmbeddings(String query, int topK) {
        try {
            List<Integer> similarRecordIds = embeddingService.findSimilarRecords(query, topK);
            
            if (similarRecordIds.isEmpty()) {
                return ChatResponse.error("No similar records found");
            }

            List<Base> results = new java.util.ArrayList<>();
            for (Integer recordId : similarRecordIds) {
                Base record = inMemoryDataCache.getRecordById(recordId);
                if (record != null) {
                    results.add(record);
                }
            }

            String sqlQuery = String.format(
                "SELECT * FROM vps_liontest_db.base WHERE id IN (%s)",
                String.join(",", similarRecordIds.stream().map(String::valueOf).toList())
            );

            String response = String.format("Found %d similar records based on semantic search", results.size());

            return ChatResponse.success(response, sqlQuery, results);

        } catch (Exception e) {
            log.error("Error in embedding search: {}", e.getMessage(), e);
            return ChatResponse.error("Failed to search with embeddings: " + e.getMessage());
        }
    }
}
