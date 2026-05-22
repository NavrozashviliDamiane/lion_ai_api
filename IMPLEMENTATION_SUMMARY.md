# Chat AI Implementation Summary

## Overview
A complete Chat AI system with Spring AI integration for analyzing car auction and logistics data. The system uses Caffeine cache for fast data retrieval and Retrieval Augmented Generation (RAG) with Georgian language support.

## Architecture

### 1. Data Models
- **CarRecord** (`model/CarRecord.java`) - Dynamic model for flexible JSON data from database
- **FieldDescription** (`model/FieldDescription.java`) - Field metadata with Georgian descriptions

### 2. Services

#### DatabaseService
- Fetches car records from stored procedure: `CALL PROC_GET_JSON(authorId)`
- Handles JSON deserialization
- Default authorId: 1748

#### CacheService
- Manages Caffeine cache with 1000 max size and 1-hour expiration
- Caches baseRecords by authorId
- Provides cache invalidation methods
- Serializes records for LLM context (Georgian labels)

#### FieldDescriptionService
- Loads field descriptions from `field_descriptions.json`
- Lazy-loads and caches descriptions
- Provides Georgian context for field information
- Supports field lookup by name

#### RagService
- Builds context for RAG by combining:
  - System prompt (Georgian instructions)
  - Field descriptions (Georgian labels)
  - Car records (from cache)
- Supports filtered context for specific fields
- Handles custom system prompts

#### ChatAiService
- Integrates with Spring AI ChatClient
- Three chat methods:
  1. `chat()` - Full context
  2. `chatWithFilteredContext()` - Specific fields only
  3. `chatWithCustomSystemPrompt()` - Custom instructions
- All responses in Georgian

### 3. Controllers

#### ChatController
REST endpoints:
- `POST /api/v1/chat/ask` - Ask question with optional filtering
- `POST /api/v1/chat/ask-filtered` - Ask with specific fields
- `POST /api/v1/chat/invalidate-cache/{authorId}` - Clear cache
- `POST /api/v1/chat/invalidate-all-caches` - Clear all caches

### 4. Configurations

#### CacheConfig
- Caffeine cache manager
- Cache names: baseRecords, embeddings, queryResults
- 1000 max entries, 1-hour TTL

#### ChatClientConfig
- Spring AI ChatClient bean

#### JacksonConfig
- ObjectMapper configuration

## Georgian Language Support

All system prompts and context are in Georgian:

### System Prompt (Georgian)
```
თქვენ ხართ ლიონ აი ასისტენტი, რომელიც სპეციალიზირებულია ავტომობილის აუქციონის და ლოგისტიკის მონაცემების ანალიზში.

მნიშვნელოვანი წესები:
1. ყველა პასუხი ქართულ ენაზე
2. მხოლოდ მოცემული მონაცემების გამოყენება
3. მოკლე, კონკრეტული და ნათელი პასუხები
4. თუ ინფორმაცია არ არის - აცხადეთ "ეს ინფორმაცია არ არის ხელმისაწვდომი"
5. ფინანსური ინფორმაციის შემთხვევაში მიუთითეთ ვალუტა (დოლარი)
```

### Key Fields (Georgian)
- manufacturer/model/year: მანქანის ტიპი
- author: დილერის სახელი
- warehouse: საწყობი ამერიკაში
- auction_pay: აუქციონის გადასახადი
- f1: ჯამური ღირებულება
- f2: დარჩენილი თანხა
- expect_delivery: სავარაუდო მიწოდება
- state/zip_number: მდებარეობა ამერიკაში
- container_amount: კონტეინერის ღირებულება
- pm_1 დან pm_5: გადახდები

## API Usage Examples

### Basic Query
```bash
curl -X POST http://localhost:8080/api/v1/chat/ask \
  -H "Content-Type: application/json" \
  -d '{
    "query": "რა დაჯდა ვოლკსვაგენ პასატი?",
    "authorId": 1748
  }'
```

### Filtered Query
```bash
curl -X POST http://localhost:8080/api/v1/chat/ask-filtered \
  -H "Content-Type: application/json" \
  -d '{
    "query": "რა არის ჯამური ღირებულება?",
    "authorId": 1748,
    "relevantFields": ["manufacturer", "model", "f1", "f2"]
  }'
```

### Custom System Prompt
```bash
curl -X POST http://localhost:8080/api/v1/chat/ask \
  -H "Content-Type: application/json" \
  -d '{
    "query": "შეაჯამე ყველა მანქანა",
    "authorId": 1748,
    "customSystemPrompt": "თქვენ ხართ ფინანსური ანალიტიკოსი. მოიტანეთ დეტალური ღირებულების ანალიზი."
  }'
```

## Performance Optimizations

1. **Caffeine Cache** - Fast in-memory caching with TTL
2. **Lazy Loading** - Field descriptions loaded on first use
3. **Filtered Context** - Option to include only relevant fields
4. **Efficient Serialization** - Minimal data transfer to LLM

## Dependencies

- Spring Boot 3.4.1
- Spring AI 1.0.0-M5
- OpenAI ChatGPT-4o
- Caffeine Cache
- Lombok
- MariaDB JDBC

## Configuration (application.properties)

```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o
spring.ai.openai.chat.options.temperature=0.3
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=1h
```

## File Structure

```
src/main/java/levani/ai/app/lionapp/
├── model/
│   ├── CarRecord.java
│   └── FieldDescription.java
├── service/
│   ├── DatabaseService.java
│   ├── CacheService.java
│   ├── FieldDescriptionService.java
│   ├── RagService.java
│   └── ChatAiService.java
├── controller/
│   └── ChatController.java
└── config/
    ├── CacheConfig.java
    ├── ChatClientConfig.java
    └── JacksonConfig.java
```

## Next Steps

1. Set `OPENAI_API_KEY` environment variable
2. Verify database connection to MariaDB
3. Test endpoints with sample queries
4. Monitor cache hit rates and performance
5. Adjust temperature and model parameters as needed

## Notes

- All responses are in Georgian language
- System uses GPT-4o for optimal accuracy
- Temperature set to 0.3 for factual, consistent responses
- Cache automatically invalidates after 1 hour
- Supports dynamic field filtering for optimized LLM queries
