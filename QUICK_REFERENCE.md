# ლიონ აი - Quick Reference

## 🚀 Quick Start

### 1. Set API Key
```bash
export OPENAI_API_KEY=sk-...
```

### 2. Build & Run
```bash
mvn clean package
mvn spring-boot:run
```

### 3. Test API
```bash
curl -X POST http://localhost:8080/api/v1/chat/ask \
  -H "Content-Type: application/json" \
  -d '{"query":"რა დაჯდა ვოლკსვაგენ პასატი?","authorId":1748}'
```

---

## 📡 API Endpoints

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/v1/chat/ask` | Ask with full context |
| POST | `/api/v1/chat/ask-filtered` | Ask with specific fields |
| POST | `/api/v1/chat/invalidate-cache/{id}` | Clear cache for author |
| POST | `/api/v1/chat/invalidate-all-caches` | Clear all caches |

---

## 📦 Request Examples

### Basic Query
```json
{
  "query": "რა დაჯდა ვოლკსვაგენ პასატი?",
  "authorId": 1748
}
```

### Filtered Query
```json
{
  "query": "რა არის ჯამური ღირებულება?",
  "authorId": 1748,
  "relevantFields": ["manufacturer", "model", "f1", "f2"]
}
```

---

## 🔑 Popular Fields

**Car Info:** manufacturer, model, year, vin, parts
**Auction:** author, buyer_id, auction_title, stock_lot
**Logistics:** warehouse, state, zip_number, point_of_delivery
**Financial:** auction_pay, f1, f2, diler, pm_1-pm_5
**Dates:** amount_set_auction, expect_delivery, pick_up_date

---

## 💻 Frontend Code Snippets

### JavaScript
```javascript
const response = await fetch('http://localhost:8080/api/v1/chat/ask', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    query: 'რა დაჯდა ვოლკსვაგენ პასატი?',
    authorId: 1748
  })
});
const data = await response.json();
console.log(data.response);
```

### Python
```python
import requests
response = requests.post('http://localhost:8080/api/v1/chat/ask', json={
  'query': 'რა დაჯდა ვოლკსვაგენ პასატი?',
  'authorId': 1748
})
print(response.json()['response'])
```

### React
```jsx
const [response, setResponse] = useState('');
const ask = async (query) => {
  const res = await fetch('http://localhost:8080/api/v1/chat/ask', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query, authorId: 1748 })
  });
  const data = await res.json();
  setResponse(data.response);
};
```

---

## 🏗️ Project Structure

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

src/main/resources/
├── application.properties
├── field_descriptions.json
└── response_example.json
```

---

## ⚙️ Configuration

**application.properties:**
```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o
spring.ai.openai.chat.options.temperature=0.3
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=1h
```

---

## 🔄 Data Flow

```
User Query (Georgian)
    ↓
ChatController
    ↓
ChatAiService
    ↓
RagService (builds context)
    ├─ System Prompt (Georgian)
    ├─ Field Descriptions (field_descriptions.json)
    └─ Car Records (from cache/database)
    ↓
Spring AI ChatClient
    ↓
OpenAI GPT-4o
    ↓
Georgian Response
    ↓
Frontend
```

---

## 🎯 Common Queries

| Question | Expected Field |
|----------|----------------|
| "რა დაჯდა?" | auction_pay, f1 |
| "სად არის?" | warehouse, state, zip_number |
| "ვინ არის დილერი?" | author, author_id |
| "რა მოდელი?" | manufacturer, model, year |
| "რა გადახდები?" | pm_1-pm_5, f2 |
| "როდის მოვა?" | expect_delivery |

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| API Key Error | Set `OPENAI_API_KEY` environment variable |
| Database Error | Check MariaDB connection in application.properties |
| Compilation Error | Update Lombok to 1.18.32+ |
| Cache Not Working | Verify Caffeine dependency in pom.xml |
| Empty Response | Check field_descriptions.json exists in resources |

---

## 📚 Documentation

| File | For |
|------|-----|
| `IMPLEMENTATION_SUMMARY.md` | Technical overview |
| `DATA_FLOW.md` | Architecture & flow diagrams |
| `FRONTEND_INTEGRATION.md` | Frontend developers |
| `QUICK_REFERENCE.md` | Quick lookup (this file) |
| `COMPLETION_SUMMARY.md` | Project status |

---

## ✅ Checklist

- [ ] Set OPENAI_API_KEY
- [ ] Verify MariaDB connection
- [ ] Build project: `mvn clean package`
- [ ] Run application: `mvn spring-boot:run`
- [ ] Test endpoint with curl
- [ ] Integrate with frontend
- [ ] Test with Georgian queries
- [ ] Monitor cache performance

---

## 🎯 Performance Tips

1. Use `/ask-filtered` for specific fields
2. Cache TTL is 1 hour (adjust if needed)
3. Temperature is 0.3 (factual responses)
4. Max cache size is 1000 entries
5. Batch related queries together

---

## 🔐 Security

- Keep OPENAI_API_KEY secret
- Validate input on frontend
- Implement rate limiting
- Use HTTPS in production
- Configure CORS if needed

---

## 📞 Need Help?

1. Check `FRONTEND_INTEGRATION.md` for API details
2. Review `RagService.java` for system prompt
3. See `DATA_FLOW.md` for architecture
4. Check logs: `tail -f target/logs/spring.log`

---

## 🚀 Ready to Deploy?

1. Build: `mvn clean package`
2. Set environment variables
3. Run: `java -jar target/lionapp-0.0.1-SNAPSHOT.jar`
4. Test: `curl http://localhost:8080/api/v1/chat/ask ...`
5. Monitor: Check logs and cache stats

---

**Last Updated:** 2026-05-22
**Status:** ✅ Complete & Ready
