# LionApp - Car Dealer Admin Inventory Management Chat API

## Overview
LionApp is a **conversational AI-powered chat system** designed specifically for **Car Dealer Admins** to manage their vehicle inventory using natural language. Instead of navigating complex admin dashboards, dealers can simply ask questions about their inventory in Georgian or English. The API loads all records into memory on startup and translates natural language queries into SQL-like filters executed against the in-memory cache. This eliminates database connection issues and provides blazing-fast query responses.

## Purpose
This is a **Car Dealer Admin Management Page** (not a client-facing page). It helps car dealers:
- Track their vehicle inventory
- Monitor vehicle status and dispatch
- Analyze sales and pricing data
- Manage operational information
- Get quick insights about their fleet

## Base URL
```
http://localhost:8080
```

## Authentication
No authentication required for current implementation.

## Content-Type
All POST requests must include:
```
Content-Type: application/json
```

## Character Encoding
All requests and responses use UTF-8 encoding to support Georgian characters.

---

## Architecture

### In-Memory Cache Design
LionApp uses a **zero-database-latency** architecture:

1. **Startup Phase**:
   - All records are loaded from MariaDB into memory (`InMemoryDataCache`)
   - Embeddings are generated for semantic search (`EmbeddingService`)
   - Session management is initialized (`ChatSessionService`)

2. **Runtime Phase**:
   - All queries are executed against in-memory cache
   - No JDBC connections are made during API calls
   - Eliminates connection pool exhaustion and timeouts
   - Provides sub-millisecond query responses

3. **Data Refresh**:
   - Optional endpoint to refresh cache from database
   - Useful for periodic data synchronization

### Performance Benefits
- **No Connection Timeouts**: In-memory queries eliminate HikariPool connection issues
- **Fast Responses**: Sub-millisecond query execution
- **Scalability**: Handles thousands of concurrent requests without database strain
- **Reliability**: No network latency or database unavailability issues

---

## Endpoints

### 1. Natural Language Query Processing
**POST** `/api/chat/query`

Translates natural language (Georgian or English) into SQL and executes database queries.

#### Request Body
```json
{
  "query": "მაჩვენე ყველა ჩანაწერი ავტორი 2212-სთვის",
  "authorId": 2212
}
```

#### Parameters
- `query` (string, required): Natural language query in Georgian or English
- `authorId` (integer, optional): Filter results by specific author ID

#### Response Format
```json
{
  "query": "მაჩვენე ყველა ჩანაწერი ავტორი 2212-სთვის",
  "sqlQuery": "SELECT * FROM base WHERE author = 2212",
  "response": "ნაპოვნია 15 ჩანაწერი ავტორი 2212-სთვის",
  "results": [
    {
      "id": 1,
      "author": 2212,
      "date": "2024-01-15",
      "buyerId": "BUYER123",
      "manufacturer": 1,
      "model": 1001,
      "year": "2020",
      "vin": "1HGCM82633A123456",
      "auctionPay": 1500.00,
      "dispatchStatus": "New",
      "containerNumber": "CONT123456",
      "branch": "Tbilisi",
      "parts": "Engine parts, transmission"
    }
  ],
  "resultCount": 15,
  "error": null
}
```

#### Error Response
```json
{
  "query": "მაჩვენე ყველა ჩანაწერი",
  "sqlQuery": null,
  "response": null,
  "results": [],
  "resultCount": 0,
  "error": "Failed to process query: Invalid SQL syntax"
}
```

---

### 2. Semantic Search
**POST** `/api/chat/search`

Performs semantic search using AI embeddings to find similar records based on natural language queries.

#### Request Parameters (Query String)
```
/api/chat/search?query=მანქანები 2020 წლის&topK=10
```

#### Parameters
- `query` (string, required): Search query in Georgian or English
- `topK` (integer, optional): Number of results to return (default: 10)

#### Response Format
```json
{
  "query": "მანქანები 2020 წლის",
  "sqlQuery": null,
  "response": "ნაპოვნია 8 მანქანა 2020 წლის მოდელის",
  "results": [
    {
      "id": 123,
      "author": 2212,
      "date": "2024-01-15",
      "buyerId": "BUYER456",
      "manufacturer": 1,
      "model": 1001,
      "year": "2020",
      "vin": "1HGCM82633A789012",
      "auctionPay": 2500.00,
      "dispatchStatus": "Pickup",
      "containerNumber": "CONT789012",
      "branch": "Batumi",
      "parts": "Body parts, lights"
    }
  ],
  "resultCount": 8,
  "error": null
}
```

#### Error Response (No API Key)
```json
{
  "query": "მანქანები 2020 წლის",
  "sqlQuery": null,
  "response": null,
  "results": [],
  "resultCount": 0,
  "error": "OpenAI API key not configured. Semantic search requires an API key."
}
```

---

### 3. Health Check
**GET** `/api/chat/health`

Checks if the API service is running.

#### Response
```
AI Chat Service is running
```

#### Response Headers
```
Content-Type: text/plain;charset=UTF-8
Status: 200 OK
```

---

## Language Support

### Georgian Examples
```javascript
// Georgian queries
const queries = [
  "მაჩვენე ყველა ჩანაწერი ავტორი 2212-სთვის",
  "იპოვე მანქანები 2020 წლიდან 2023 წლამდე",
  "ჩანაწერები დიდი აუქციონის გადახდით",
  "რა არის პიკაპის სტატუსით",
  "კონტეინერის ნომერი შეიცავს ABC",
  "გასული თვის ჩანაწერები",
  "რამდენი მანქანაა საწყობში"
];
```

### English Examples
```javascript
// English queries
const queries = [
  "Show all records for author 2212",
  "Find cars from 2020 to 2023",
  "Records with high auction payments",
  "What records have Pickup status",
  "Container number contains ABC",
  "Records from last month",
  "How many cars are in warehouse"
];
```

---


---

### 4. Create Chat Session
**POST** `/api/chat/session/create`

Creates a new chat session to maintain conversation context across multiple queries.

#### Response Format
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### 5. Query with Session Context (Conversational AI)
**POST** `/api/chat/session/{sessionId}/query`

Processes a query while maintaining full conversation history and context. The AI understands previous questions and provides natural, human-like responses.

#### Request Body
```json
{
  "query": "მაჩვენე ყველა ჩანაწერი ავტორი 2212-სთვის",
  "authorId": 2212
}
```

#### Response Format
```json
{
  "query": "მაჩვენე ყველა ჩანაწერი ავტორი 2212-სთვის",
  "sqlQuery": "SELECT * FROM base WHERE author = 2212",
  "response": "ნაპოვნია 15 ჩანაწერი ავტორი 2212-სთვის. ეს ჩანაწერები მოიცავს სხვადსხვა წლის მანქანებს, რომელთა ყველაზე ახალი არის 2023 წელი.",
  "results": [...],
  "resultCount": 15,
  "error": null
}
```

#### Conversational Features
- **Context Awareness**: AI remembers all previous messages in the session
- **Natural Responses**: ChatGPT-style answers, not raw data dumps
- **Flexible & Casual**: Responses are natural and conversational, not structured
- **Engaging Tone**: Friendly, enthusiastic, and accessible language
- **Smart Data Handling**: AI extracts relevant information from all available data
- **Follow-up Questions**: Ask naturally without repeating context
- **Language Consistency**: Responds in the same language as your query (Georgian or English)
- **Concise Answers**: 1-3 sentence responses, natural and casual
- **Frontend Flexibility**: Frontend can query and display any data from results and embeddings
- **Georgian Grammar**: Grammatically correct Georgian with proper verb tenses, cases, and prepositions
- **Natural Georgian Expressions**: Uses authentic Georgian phrases, not literal translations

#### Georgian Language Grammar Rules
The AI follows these Georgian grammar rules for accurate responses:
1. **Verb Tenses**: Correct use of აორისტი (aorist), პრეზენტი (present), პერფექტი (perfect)
2. **Direct Answers**: Questions are answered directly first, then explained
3. **Natural Expressions**: Uses authentic Georgian phrases, not literal translations
4. **Noun Cases**: Proper nominative, genitive, dative, accusative, instrumental, locative cases
5. **Adjective Forms**: Correct comparative and superlative forms
6. **Possessive Forms**: Correct use of მაქვს/მაქვთ/აქვს/აქვთ (have forms)
7. **Number Agreement**: Correct singular/plural forms (არის/არიან)
8. **Prepositions**: Proper use of ში (in), ზე (on), ის (of), თან (with), მიერ (by)

#### Example Conversation Flow (Car Dealer Admin Context)
```
Dealer Admin: "მითხარი ჩემი ინვენტარიდან ყველაზე ახალი მანქანა რომელია"
AI: "თქვენი ინვენტარიდან ყველაზე ახალი მანქანა არის 2025 წლის Hyundai Elantra, რომელიც 'New' სტატუსით არის. მისი აუქციონის ფასი იყო 3500 დოლარი."

Dealer Admin: "რამდენი მანქანა გამოსაგზავნი აქვს ჩემს სიაში"
AI: "თქვენ გაქვთ 12 მანქანა 'Order Taken' სტატუსით, რომელიც გამოსაგზავნი არის. მათ შორის 5 არის 2021 წელი და 7 არის 2020 წელი."

Dealer Admin: "რომელი მანქანაა ყველაზე ძვირი ჩემი ინვენტარიდან"
AI: "ყველაზე ძვირი მანქანა თქვენი ინვენტარიდან 5565 დოლარი ღირდა. ეს 2021 წელი გამოშვებული Toyota იყო, რომელიც ამჟამად 'Deliver' სტატუსით არის."
```

#### Response Structure
The response contains both the AI's conversational response AND the raw data for frontend flexibility:

```json
{
  "naturalLanguageResponse": "ყველაზე ახალი მანქანა არის 2025 წლის Hyundai Elantra...",
  "generatedQuery": "SELECT * FROM vps_liontest_db.base WHERE...",
  "results": [
    {
      "id": 470928,
      "year": "2025",
      "manufacturerName": "Hyundai",
      "modelName": "Elantra",
      "auctionPay": 3500.00,
      "dispatchStatus": "New",
      "vin": "3VVNX7B21PM308313",
      "date": "2026-05-08",
      ...
    }
  ],
  "resultCount": 1,
  "error": null
}
```

#### Frontend Integration
The frontend can:
1. **Display AI Response**: Show `naturalLanguageResponse` as the main chat message
2. **Query Results**: Access `results` array for any custom data display
3. **Use Embeddings**: Query embeddings endpoint for semantic search
4. **Build Custom UI**: Display data however needed (tables, cards, lists, etc.)

#### Response Headers
```
X-Session-Id: 550e8400-e29b-41d4-a716-446655440000
X-Session-Created: true
```

#### Auto-Session Creation
If the provided `sessionId` doesn't exist, the system automatically:
- Creates a new session
- Processes your query normally

---

## OpenAI Usage & Billing Endpoints

### Get Current Month Usage
```
GET /api/chat/openai/usage/current-month
```

**Response:**
```json
{
  "month": "2026-05",
  "data": {
    "object": "list",
    "data": [
      {
        "timestamp": 1715817600,
        "line_items": [
          {
            "name": "GPT-4 usage",
            "quantity": 1234.56,
            "unit": "1K tokens",
            "unit_amount": 0.03,
            "currency": "usd",
            "cost": 37.04
          }
        ]
      }
    ],
    "total_usage": 37.04
  },
  "timestamp": 1715817600000
}
```

### Get Usage for Specific Month
```
GET /api/chat/openai/usage/month/{year}/{month}
```

**Example:**
```
GET /api/chat/openai/usage/month/2026/05
```

**Note:** The endpoint uses the first day of the specified month (e.g., 2026-05-01) to query OpenAI's usage API.

**Response:** Same format as current month usage

### Get Billing Information
```
GET /api/chat/openai/billing/info
```

**Response:**
```json
{
  "billingInfo": {
    "object": "billing_credit_grants",
    "data": [
      {
        "id": "grant_123",
        "object": "billing_credit_grant",
        "created_at": 1715817600,
        "expires_at": 1747353600,
        "grant_amount": 100.00,
        "used_amount": 37.04,
        "currency": "usd"
      }
    ]
  },
  "timestamp": 1715817600000
}
```

### Get Subscription Information
```
GET /api/chat/openai/subscription/info
```

**Response:**
```json
{
  "subscription": {
    "object": "billing_subscription",
    "has_payment_method": true,
    "canceled_at": null,
    "delinquent": false,
    "access_until": 1747353600,
    "soft_limit": 100,
    "hard_limit": 1000,
    "plan": {
      "id": "plan_123",
      "object": "billing_plan",
      "title": "Pay as you go",
      "created_at": 1715817600
    }
  },
  "timestamp": 1715817600000
}
```

### Get Available Models
```
GET /api/chat/openai/models
```

**Response:**
```json
{
  "models": {
    "object": "list",
    "data": [
      {
        "id": "gpt-4",
        "object": "model",
        "created": 1677610602,
        "owned_by": "openai"
      },
      {
        "id": "gpt-4-turbo",
        "object": "model",
        "created": 1677610602,
        "owned_by": "openai"
      },
      {
        "id": "gpt-3.5-turbo",
        "object": "model",
        "created": 1677610602,
        "owned_by": "openai"
      }
    ]
  },
  "timestamp": 1715817600000
}
```

### Important Notes

⚠️ **API Key Required**: These endpoints require a valid OpenAI API key set in the `OPENAI_API_KEY` environment variable.

⚠️ **Permissions**: Your OpenAI API key must have billing/usage read permissions. This typically requires:
- Organization admin or billing admin role
- Or a dedicated API key with billing scope

⚠️ **Rate Limits**: OpenAI's billing API has rate limits. Avoid calling these endpoints too frequently.

✅ **Usage Tracking**: Use the current month endpoint to monitor your API costs in real-time.

✅ **Billing Alerts**: Check subscription info to ensure you have active payment methods and haven't hit hard limits.
- Returns the new session ID in the `X-Session-Id` header
- Sets `X-Session-Created: true` to indicate a new session was created

**No error is returned** - the conversation continues seamlessly!

#### Example Flow with Invalid Session
```http
POST /api/chat/session/invalid-id/query
Body: {"query": "მომიძებნე მანქანები"}

Response Headers:
X-Session-Id: 550e8400-e29b-41d4-a716-446655440000
X-Session-Created: true

Response Body:
{
  "query": "მომიძებნე მანქანები",
  "sqlQuery": "SELECT * FROM base ORDER BY date DESC LIMIT 100",
  "response": "ნაპოვნია 100 ჩანაწერი. ეს მოიცავს სხვადსხვა წლის მანქანებს.",
  "results": [...],
  "resultCount": 100,
  "error": null
}
```

---

### 6. Get Session Info
**GET** `/api/chat/session/{sessionId}`

Retrieves information about an active chat session.

#### Response Format
```json
{
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2026-05-17T16:40:00",
  "lastAccessedAt": "2026-05-17T16:45:30",
  "language": "ka",
  "messageCount": 5
}
```

---

### 7. Set Session Language
**POST** `/api/chat/session/{sessionId}/language/{language}`

Sets the language preference for a session (e.g., "ka" for Georgian, "en" for English).

#### Response Format
```json
{
  "language": "ka"
}
```

---

### 8. Delete Session
**DELETE** `/api/chat/session/{sessionId}`

Closes and removes a chat session.

#### Response
```
204 No Content
```

---

### 9. Get Active Sessions Count
**GET** `/api/chat/sessions/active`

Retrieves the number of currently active chat sessions.

#### Response Format
```json
{
  "activeSessionCount": 5
}
```

---

## Session Management Flow

### Step 1: Create Session
```bash
POST /api/chat/session/create
```
Response: `{"sessionId": "550e8400-e29b-41d4-a716-446655440000"}`

### Step 2: Set Language (Optional)
```bash
POST /api/chat/session/{sessionId}/language/ka
```

### Step 3: Send Queries with Conversational AI
```bash
POST /api/chat/session/{sessionId}/query
Body: {"query": "your query here"}
```
The AI will:
- Understand context from previous messages
- Provide natural, human-like responses
- Remember what you asked before
- Respond in your language (Georgian or English)

### Step 4: Get Session Info
```bash
GET /api/chat/session/{sessionId}
```

### Step 5: Delete Session When Done
```bash
DELETE /api/chat/session/{sessionId}
```

---

## Session Features

- **Conversational AI**: AI understands full conversation context and history
- **Persistent Context**: All messages in a session are stored and used for context
- **Language Tracking**: Each session maintains its language preference
- **Auto-Cleanup**: Sessions expire after 60 minutes of inactivity
- **Concurrent Support**: Multiple sessions can run simultaneously
- **Message History**: Full conversation history is maintained per session
- **Natural Responses**: Answers are human-like, not just raw database results
- **Follow-up Understanding**: Ask follow-up questions naturally without repeating context

---

## Error Handling

### Common HTTP Status Codes
- `200 OK`: Request successful
- `400 Bad Request`: Invalid request parameters or malformed query
- `500 Internal Server Error`: Server error or database connection issue

### Error Response Format
```json
{
  "error": "Error description",
  "query": "Original query",
  "sqlQuery": null,
  "response": null,
  "results": [],
  "resultCount": 0
}
```

### Frontend Error Handling
```javascript
try {
  const result = await api.processQuery(query);
  // Handle success
} catch (error) {
  if (error.message.includes('API key')) {
    // Handle missing API key for semantic search
    showWarning('Semantic search requires API key configuration');
  } else if (error.message.includes('Invalid SQL')) {
    // Handle invalid query
    showError('Invalid query. Please try a different phrasing.');
  } else {
    // Handle other errors
    showError('Server error. Please try again later.');
  }
}
```

---

## Rate Limiting & Performance

### Caching
- Query results are cached using Caffeine cache
- Cache expires after 1 hour
- Maximum 1000 cached entries

### Performance Tips
1. Use specific queries instead of broad searches
2. Include authorId when possible for better filtering
3. Semantic search requires OpenAI API calls - use judiciously
4. Cache results on frontend for repeated queries

---

## Data Model

### Base Record Fields
```json
{
  "id": "integer - Record ID",
  "author": "integer - Author ID",
  "date": "date - Record date (YYYY-MM-DD)",
  "buyerId": "string - Buyer identifier",
  "manufacturer": "integer - Manufacturer code",
  "model": "integer - Model code",
  "year": "string - Vehicle year",
  "vin": "string - Vehicle VIN number",
  "auctionPay": "decimal - Auction payment amount",
  "dispatchStatus": "string - Dispatch status (New, On Site, Order Taken, Pickup, Deliver)",
  "containerNumber": "string - Container number",
  "branch": "string - Branch location",
  "parts": "string - Parts description"
}
```

---

## Testing

### Sample Requests - Stateless (Legacy)
```bash
# Georgian query
curl -X POST http://localhost:8080/api/chat/query \
  -H "Content-Type: application/json" \
  -d '{"query":"მაჩვენე ყველა ჩანაწერი ავტორი 2212-სთვის","authorId":2212}'

# English query
curl -X POST http://localhost:8080/api/chat/query \
  -H "Content-Type: application/json" \
  -d '{"query":"Show all records for author 2212","authorId":2212}'

# Semantic search
curl -X POST "http://localhost:8080/api/chat/search?query=2020%20cars&topK=5"

# Health check
curl -X GET http://localhost:8080/api/chat/health
```

### Sample Requests - Session-Based (Recommended)
```bash
# 1. Create a new session
SESSION_ID=$(curl -X POST http://localhost:8080/api/chat/session/create | jq -r '.sessionId')
echo "Session ID: $SESSION_ID"

# 2. Set session language to Georgian
curl -X POST http://localhost:8080/api/chat/session/$SESSION_ID/language/ka

# 3. Send first query
curl -X POST http://localhost:8080/api/chat/session/$SESSION_ID/query \
  -H "Content-Type: application/json" \
  -d '{"query":"მაჩვენე ყველა ჩანაწერი ავტორი 2212-სთვის","authorId":2212}'

# 4. Send follow-up query (maintains context)
curl -X POST http://localhost:8080/api/chat/session/$SESSION_ID/query \
  -H "Content-Type: application/json" \
  -d '{"query":"რა არის პიკაპის სტატუსით"}'

# 5. Get session info
curl -X GET http://localhost:8080/api/chat/session/$SESSION_ID

# 6. Get active sessions count
curl -X GET http://localhost:8080/api/chat/sessions/active

# 7. Delete session when done
curl -X DELETE http://localhost:8080/api/chat/session/$SESSION_ID
```

---

## Support

For technical issues or questions about the API:
1. Check the application logs for detailed error messages
2. Verify database connection and OpenAI API key configuration
3. Test with the health check endpoint first
4. Review query syntax and ensure proper UTF-8 encoding for Georgian text
