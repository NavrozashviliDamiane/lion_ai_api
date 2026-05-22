# ლიონ აი - Frontend Integration Guide

## 📱 Frontend Integration Overview

This guide explains how to integrate the Chat AI API into your frontend application.

---

## 🔌 API Base URL

```
http://localhost:8080/api/v1/chat
```

---

## 📡 Endpoints

### 1. Basic Chat Query

**Endpoint:** `POST /api/v1/chat/ask`

**Description:** Ask a question with full context (all fields and records)

**Request:**
```json
{
  "query": "რა დაჯდა ვოლკსვაგენ პასატი?",
  "authorId": 1748
}
```

**Response:**
```json
{
  "response": "ვოლკსვაგენ პასატი (2014) აუქციონზე გაიყიდა 1285 დოლარად. ჯამური ღირებულება ყველა ხარჯის ჩათვლით 2595 დოლარი იყო.",
  "query": "რა დაჯდა ვოლკსვაგენ პასატი?",
  "authorId": 1748,
  "timestamp": "2026-05-22T19:30:00",
  "success": true,
  "errorMessage": null
}
```

**JavaScript/TypeScript Example:**
```javascript
const askQuestion = async (query, authorId = 1748) => {
  try {
    const response = await fetch('http://localhost:8080/api/v1/chat/ask', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        query: query,
        authorId: authorId
      })
    });

    const data = await response.json();
    
    if (data.success) {
      console.log('Response:', data.response);
      return data.response;
    } else {
      console.error('Error:', data.errorMessage);
      return null;
    }
  } catch (error) {
    console.error('API Error:', error);
    return null;
  }
};

// Usage
askQuestion('რა დაჯდა ვოლკსვაგენ პასატი?');
```

**Python Example:**
```python
import requests
import json

def ask_question(query, author_id=1748):
    url = 'http://localhost:8080/api/v1/chat/ask'
    payload = {
        'query': query,
        'authorId': author_id
    }
    
    response = requests.post(url, json=payload)
    data = response.json()
    
    if data['success']:
        print('Response:', data['response'])
        return data['response']
    else:
        print('Error:', data['errorMessage'])
        return None

# Usage
ask_question('რა დაჯდა ვოლკსვაგენ პასატი?')
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/v1/chat/ask \
  -H "Content-Type: application/json" \
  -d '{
    "query": "რა დაჯდა ვოლკსვაგენ პასატი?",
    "authorId": 1748
  }'
```

---

### 2. Filtered Chat Query

**Endpoint:** `POST /api/v1/chat/ask-filtered`

**Description:** Ask a question with only specific fields (optimized for performance)

**Request:**
```json
{
  "query": "რა არის ჯამური ღირებულება?",
  "authorId": 1748,
  "relevantFields": [
    "manufacturer",
    "model",
    "year",
    "f1",
    "f2",
    "auction_pay"
  ]
}
```

**Response:**
```json
{
  "response": "ვოლკსვაგენ პასატი (2014): ჯამური ღირებულება 2595 დოლარი, აუქციონის გადასახადი 1285 დოლარი, დარჩენილი თანხა -2595 დოლარი.",
  "query": "რა არის ჯამური ღირებულება?",
  "authorId": 1748,
  "timestamp": "2026-05-22T19:30:00",
  "success": true,
  "errorMessage": null
}
```

**JavaScript/TypeScript Example:**
```javascript
const askFilteredQuestion = async (query, relevantFields, authorId = 1748) => {
  try {
    const response = await fetch('http://localhost:8080/api/v1/chat/ask-filtered', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        query: query,
        authorId: authorId,
        relevantFields: relevantFields
      })
    });

    const data = await response.json();
    
    if (data.success) {
      return data.response;
    } else {
      console.error('Error:', data.errorMessage);
      return null;
    }
  } catch (error) {
    console.error('API Error:', error);
    return null;
  }
};

// Usage
const fields = ['manufacturer', 'model', 'year', 'f1', 'f2', 'auction_pay'];
askFilteredQuestion('რა არის ჯამური ღირებულება?', fields);
```

**Python Example:**
```python
def ask_filtered_question(query, relevant_fields, author_id=1748):
    url = 'http://localhost:8080/api/v1/chat/ask-filtered'
    payload = {
        'query': query,
        'authorId': author_id,
        'relevantFields': relevant_fields
    }
    
    response = requests.post(url, json=payload)
    data = response.json()
    
    if data['success']:
        return data['response']
    else:
        print('Error:', data['errorMessage'])
        return None

# Usage
fields = ['manufacturer', 'model', 'year', 'f1', 'f2', 'auction_pay']
ask_filtered_question('რა არის ჯამური ღირებულება?', fields)
```

---

### 3. Cache Invalidation

**Endpoint:** `POST /api/v1/chat/invalidate-cache/{authorId}`

**Description:** Clear cache for specific author

**Request:**
```bash
POST /api/v1/chat/invalidate-cache/1748
```

**Response:**
```json
"Cache invalidated for authorId: 1748"
```

**JavaScript Example:**
```javascript
const invalidateCache = async (authorId = 1748) => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/chat/invalidate-cache/${authorId}`,
      { method: 'POST' }
    );
    const message = await response.text();
    console.log(message);
  } catch (error) {
    console.error('Error:', error);
  }
};
```

---

### 4. Invalidate All Caches

**Endpoint:** `POST /api/v1/chat/invalidate-all-caches`

**Description:** Clear all caches

**Request:**
```bash
POST /api/v1/chat/invalidate-all-caches
```

**Response:**
```json
"All caches invalidated"
```

**JavaScript Example:**
```javascript
const invalidateAllCaches = async () => {
  try {
    const response = await fetch(
      'http://localhost:8080/api/v1/chat/invalidate-all-caches',
      { method: 'POST' }
    );
    const message = await response.text();
    console.log(message);
  } catch (error) {
    console.error('Error:', error);
  }
};
```

---

## 🎯 Common Use Cases

### Use Case 1: Simple Chat Interface

```javascript
class ChatAI {
  constructor(baseUrl = 'http://localhost:8080/api/v1/chat') {
    this.baseUrl = baseUrl;
  }

  async ask(query, authorId = 1748) {
    const response = await fetch(`${this.baseUrl}/ask`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query, authorId })
    });
    
    const data = await response.json();
    return data.success ? data.response : null;
  }

  async askFiltered(query, fields, authorId = 1748) {
    const response = await fetch(`${this.baseUrl}/ask-filtered`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query, authorId, relevantFields: fields })
    });
    
    const data = await response.json();
    return data.success ? data.response : null;
  }
}

// Usage
const chatAI = new ChatAI();
const answer = await chatAI.ask('რა დაჯდა ვოლკსვაგენ პასატი?');
console.log(answer);
```

### Use Case 2: React Component

```jsx
import React, { useState } from 'react';

const ChatAIComponent = () => {
  const [query, setQuery] = useState('');
  const [response, setResponse] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleAsk = async () => {
    setLoading(true);
    setError('');
    
    try {
      const res = await fetch('http://localhost:8080/api/v1/chat/ask', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          query: query,
          authorId: 1748
        })
      });

      const data = await res.json();
      
      if (data.success) {
        setResponse(data.response);
      } else {
        setError(data.errorMessage || 'Unknown error');
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="chat-container">
      <h1>ლიონ აი - Chat</h1>
      
      <textarea
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="დაწერეთ თქვენი კითხვა..."
        rows="4"
      />
      
      <button onClick={handleAsk} disabled={loading}>
        {loading ? 'იტვირთება...' : 'კითხვა'}
      </button>
      
      {error && <div className="error">{error}</div>}
      {response && <div className="response">{response}</div>}
    </div>
  );
};

export default ChatAIComponent;
```

### Use Case 3: Vue.js Component

```vue
<template>
  <div class="chat-container">
    <h1>ლიონ აი - Chat</h1>
    
    <textarea
      v-model="query"
      placeholder="დაწერეთ თქვენი კითხვა..."
      rows="4"
    />
    
    <button @click="askQuestion" :disabled="loading">
      {{ loading ? 'იტვირთება...' : 'კითხვა' }}
    </button>
    
    <div v-if="error" class="error">{{ error }}</div>
    <div v-if="response" class="response">{{ response }}</div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      query: '',
      response: '',
      loading: false,
      error: ''
    };
  },
  methods: {
    async askQuestion() {
      this.loading = true;
      this.error = '';
      
      try {
        const res = await fetch('http://localhost:8080/api/v1/chat/ask', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            query: this.query,
            authorId: 1748
          })
        });

        const data = await res.json();
        
        if (data.success) {
          this.response = data.response;
        } else {
          this.error = data.errorMessage || 'Unknown error';
        }
      } catch (err) {
        this.error = err.message;
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
```

---

## 📋 Request/Response Models

### ChatRequest
```json
{
  "query": "string (required) - The question in Georgian",
  "authorId": "integer (optional) - Default: 1748",
  "relevantFields": "array[string] (optional) - Specific fields to include",
  "customSystemPrompt": "string (optional) - Custom instructions for LLM"
}
```

### ChatResponse
```json
{
  "response": "string - The AI response in Georgian",
  "query": "string - The original query",
  "authorId": "integer - The author ID used",
  "timestamp": "string (ISO 8601) - Response timestamp",
  "success": "boolean - Whether request was successful",
  "errorMessage": "string - Error message if success is false"
}
```

---

## 🔑 Available Fields for Filtering

### Car Information
- `manufacturer` - მწარმოებელი
- `model` - მოდელი
- `year` - წელი
- `vin` - VIN კოდი
- `parts` - ნაწილები
- `car_location` - მდებარეობა

### Auction Information
- `author` - დილერი
- `author_id` - დილერის ID
- `buyer_id` - ბაიერის კოდი
- `auction_title` - აუქციონი
- `stock_lot` - ლოტი
- `record_status` - სტატუსი

### Logistics
- `warehouse` - საწყობი
- `usa_geo` - მიმართულება
- `state` - შტატი
- `zip_number` - ZIP კოდი
- `point_of_delivery` - პორტი
- `inside_transport_usa` - შიდა ტრანსპორტი
- `container_amount` - კონტეინერი
- `container_line` - კომპანია

### Financial
- `auction_pay` - აუქციონის გადასახადი
- `diler` - დილერის კომისია
- `diler2` - ქვედილერის კომისია
- `pm_1` დან `pm_5` - გადახდები
- `f1` - ჯამური ღირებულება
- `f2` - ბალანსი

### Dates
- `amount_set_auction` - აუქციონის თარიღი
- `pick_up_deadline` - დედლაინი
- `pick_up_date` - აყვანის თარიღი
- `expect_pick_up` - სავარაუდო აყვანა
- `expect_delivery` - სავარაუდო მიწოდება
- `date` - გაყიდვის თარიღი

---

## ⚠️ Error Handling

**Common Error Responses:**

```json
{
  "success": false,
  "errorMessage": "Query cannot be empty",
  "timestamp": "2026-05-22T19:30:00"
}
```

**Error Codes:**
- `400` - Bad Request (empty query, missing fields)
- `500` - Internal Server Error (database, LLM error)

**JavaScript Error Handling:**
```javascript
const askQuestion = async (query) => {
  try {
    const response = await fetch('http://localhost:8080/api/v1/chat/ask', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query, authorId: 1748 })
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    
    if (!data.success) {
      throw new Error(data.errorMessage || 'Unknown error');
    }

    return data.response;
  } catch (error) {
    console.error('Error:', error.message);
    // Show user-friendly error message
    return 'Sorry, something went wrong. Please try again.';
  }
};
```

---

## 🚀 Performance Tips

1. **Use Filtered Queries** - When you only need specific fields, use `/ask-filtered` endpoint
2. **Cache Management** - Invalidate cache only when data changes
3. **Batch Requests** - Group related questions together
4. **Error Handling** - Implement proper retry logic
5. **Loading States** - Show loading indicators during API calls

---

## 🔐 Security Considerations

1. **CORS** - Configure CORS in backend if frontend is on different domain
2. **Rate Limiting** - Implement rate limiting on frontend
3. **Input Validation** - Validate queries before sending
4. **Error Messages** - Don't expose sensitive information in errors

---

## 📞 Support

For issues or questions, check:
- `IMPLEMENTATION_SUMMARY.md` - Implementation details
- `DATA_FLOW.md` - How data flows through the system
- `RagService.java` - System prompt and context building
