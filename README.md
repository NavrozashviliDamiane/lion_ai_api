# LionApp - AI-Powered Database Query System

## Overview
AI-powered chat system that translates natural language queries into SQL queries for the MariaDB `base` table. The system uses Spring AI with OpenAI for query generation and semantic search capabilities.

## Architecture

### Components
- **Spring Boot 3.4.1** - Application framework
- **Java 21** - Programming language
- **Spring AI 1.0.0-M5** - AI integration with OpenAI
- **MariaDB** - Database
- **Caffeine Cache** - In-memory caching
- **Spring Data JPA** - Data access layer

### Features
1. **Natural Language to SQL Translation** - Convert user queries to SQL
2. **Multilingual Support** - Full support for English and Georgian (ქართული) languages
3. **In-Memory Embeddings** - Semantic search using vector embeddings
4. **Caffeine Caching** - Fast query result caching
5. **RESTful API** - Easy integration with frontend
6. **UTF-8 Encoding** - Proper handling of Georgian characters throughout the system

## Prerequisites

1. Java 21 or higher
2. MariaDB database with `vps_liontest_db` schema
3. OpenAI API key

## Configuration

### Environment Variables
Set the following environment variable:
```bash
export OPENAI_API_KEY=your_openai_api_key_here
```

### Database Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/vps_liontest_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Build and Run

### Build the project
```bash
mvn clean install
```

### Run the application
```bash
mvn spring-boot:run
```

Or with environment variable:
```bash
OPENAI_API_KEY=your_key mvn spring-boot:run
```

## API Endpoints

### 1. Process Natural Language Query
**POST** `/api/chat/query`

Request:
```json
{
  "query": "Show me all records for author 2212 from last month",
  "authorId": 2212
}
```

Response:
```json
{
  "naturalLanguageResponse": "Found 15 records for author 2212 from the last month.",
  "generatedQuery": "SELECT * FROM vps_liontest_db.base WHERE author = 2212 AND date >= '2026-04-17' ORDER BY date DESC LIMIT 100",
  "results": [...],
  "resultCount": 15,
  "error": null
}
```

### 2. Semantic Search
**POST** `/api/chat/search?query=cars with high auction prices&topK=10`

Response:
```json
{
  "naturalLanguageResponse": "Found 10 similar records based on semantic search",
  "generatedQuery": "SELECT * FROM vps_liontest_db.base WHERE id IN (1,5,12,...)",
  "results": [...],
  "resultCount": 10,
  "error": null
}
```

### 3. Health Check
**GET** `/api/chat/health`

Response:
```
AI Chat Service is running
```

## Example Queries

### ქართული მაგალითები / Georgian Examples (Primary Language)

1. **"მაჩვენე ყველა ჩანაწერი ავტორი 2212-სთვის"**
   - Shows all records for author 2212

2. **"იპოვე მანქანები 2020 წლიდან 2023 წლამდე"**
   - Find cars from 2020 to 2023

3. **"ჩანაწერები დიდი აუქციონის გადახდით"**
   - Records with high auction payments

4. **"რა არის პიკაპის სტატუსით"**
   - What records have Pickup status

5. **"კონტეინერის ნომერი შეიცავს ABC"**
   - Container number contains ABC

6. **"გასული თვის ჩანაწერები"**
   - Records from last month

7. **"მაჩვენე ყველა ჩანაწერი VIN ნომრით ABC123"**
   - Show all records with VIN ABC123

8. **"იპოვე ჩანაწერები 2024 წლის იანვრიდან"**
   - Find records from January 2024

9. **"რამდენი მანქანაა საწყობში"**
   - How many cars are in warehouse

10. **"მაჩვენე ყველაზე ძვირი აუქციონები"**
    - Show the most expensive auctions

### English Examples

1. "Show all records for author 2212"
2. "Find cars from 2020 to 2023"
3. "Get records with VIN containing ABC123"
4. "Show me the most expensive auctions"
5. "Find all records with dispatch status 'Pickup'"

## Project Structure

```
src/main/java/levani/ai/app/lionapp/
├── config/
│   └── CacheConfig.java           # Caffeine cache configuration
├── controller/
│   └── ChatController.java        # REST API endpoints
├── domain/
│   └── Base.java                  # JPA entity for base table
├── dto/
│   ├── ChatRequest.java           # Request DTO
│   └── ChatResponse.java          # Response DTO
├── repository/
│   └── BaseRepository.java        # JPA repository
├── service/
│   ├── AiQueryService.java        # AI query processing
│   └── EmbeddingService.java      # Vector embeddings management
└── LionappApplication.java        # Main application class
```

## Design Patterns Used

1. **Repository Pattern** - Data access abstraction
2. **Service Layer Pattern** - Business logic separation
3. **DTO Pattern** - Data transfer objects
4. **Builder Pattern** - Object construction (Lombok)
5. **Dependency Injection** - Spring IoC container
6. **Cache-Aside Pattern** - Caffeine caching strategy

## Performance Considerations

- **In-Memory Embeddings**: All embeddings loaded at startup for fast semantic search
- **Caffeine Cache**: Query results cached for 1 hour
- **Connection Pooling**: Managed by Spring Boot
- **Lazy Loading**: JPA entities use lazy loading where appropriate

## Security Notes

- API key stored in environment variable
- SQL injection prevented by using JPA native queries with proper validation
- Only SELECT queries allowed
- CORS enabled for frontend integration

## Future Enhancements

1. Add authentication/authorization
2. Implement query history tracking
3. Add support for complex joins
4. Implement streaming responses
5. Add query result pagination
6. Create frontend UI with React

## Troubleshooting

### Issue: Embeddings not initializing
- Check database connection
- Verify OpenAI API key is set
- Check logs for initialization errors

### Issue: SQL generation fails
- Verify OpenAI API key is valid
- Check prompt template configuration
- Review generated SQL in logs

### Issue: Cache not working
- Verify Caffeine dependency is loaded
- Check cache configuration
- Enable cache logging

## License
Proprietary
