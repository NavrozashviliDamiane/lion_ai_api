# ლიონ აი - მონაცემების ნაკადი (Data Flow)

## 📊 სრული ნაკადი

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          USER QUESTION (ქართულად)                          │
│                    "რა დაჯდა ვოლკსვაგენ პასატი?"                            │
└────────────────────────────────┬────────────────────────────────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │   ChatController       │
                    │  POST /api/v1/chat/ask │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │   ChatAiService        │
                    │  chat() method         │
                    └────────────┬───────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │   RagService           │
                    │ buildContextForQuery() │
                    └────────────┬───────────┘
                                 │
                ┌────────────────┼────────────────┐
                │                │                │
                ▼                ▼                ▼
    ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
    │ buildSystemPrompt│  │FieldDescription  │  │ CacheService     │
    │ (Georgian Rules) │  │ Service           │  │ (Car Records)    │
    │                  │  │                   │  │                  │
    │ ✅ Language      │  │ 📄 Loads:         │  │ 🔄 Fetches from: │
    │ ✅ Instructions  │  │ field_descriptions│  │ Database         │
    │ ✅ Field Guide   │  │ .json             │  │ (PROC_GET_JSON)  │
    │ ✅ Examples      │  │                   │  │                  │
    │ ✅ Rules         │  │ 📝 Builds:        │  │ 💾 Caches:       │
    │                  │  │ Georgian context  │  │ Caffeine (1h)    │
    │                  │  │ for each field    │  │                  │
    └──────────────────┘  └──────────────────┘  └──────────────────┘
                │                │                │
                └────────────────┼────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────────────┐
                    │  COMBINED CONTEXT FOR LLM      │
                    │                                │
                    │ 1. System Prompt (Georgian)    │
                    │ 2. Field Descriptions (JSON)   │
                    │ 3. Car Records (Database)      │
                    └────────────┬───────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────────────┐
                    │  Spring AI ChatClient          │
                    │  + OpenAI GPT-4o               │
                    └────────────┬───────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────────────┐
                    │  LLM RESPONSE (Georgian)       │
                    │  "ვოლკსვაგენ პასატი (2014)     │
                    │   აუქციონზე გაიყიდა 1285       │
                    │   დოლარად..."                 │
                    └────────────┬───────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────────────┐
                    │  ChatResponse (JSON)           │
                    │  - response (Georgian)         │
                    │  - query                       │
                    │  - authorId                    │
                    │  - timestamp                   │
                    │  - success flag                │
                    └────────────┬───────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────────────┐
                    │  USER RECEIVES ANSWER          │
                    │  (ქართულად)                    │
                    └────────────────────────────────┘
```

---

## 🔄 დეტალური ნაკადი - field_descriptions.json

### ნაბიჯი 1: JSON ფაილის ჩატვირთვა

**ფაილი:** `src/main/resources/field_descriptions.json`

```json
[
  {
    "id": 1,
    "field_name": "usa_geo",
    "label_name": "USA-GEO",
    "field_desc": "აღნიშნავს მანქანის ტრანსპორტირების მიმართულებას...",
    "question": "რომელი მიმართულებით მიდის მანქანა?..."
  },
  {
    "id": 6,
    "field_name": "manufacturer",
    "label_name": "Manufacture",
    "field_desc": "მანქანის მწარმოებელი კომპანიის დასახელება...",
    "question": "ვინ არის მანქანის მწარმოებელი?..."
  },
  ...
]
```

### ნაბიჯი 2: FieldDescriptionService ჩატვირთავს JSON-ს

**კოდი:** `FieldDescriptionService.loadFieldDescriptions()`

```java
public synchronized Map<String, FieldDescription> loadFieldDescriptions() {
    ClassPathResource resource = new ClassPathResource("field_descriptions.json");
    List<FieldDescription> descriptions = objectMapper.readValue(
            resource.getInputStream(),
            new TypeReference<List<FieldDescription>>() {}
    );
    
    // ქმნის Map<fieldName, FieldDescription>
    fieldDescriptionMap = new HashMap<>();
    for (FieldDescription desc : descriptions) {
        fieldDescriptionMap.put(desc.getFieldName(), desc);
    }
    return fieldDescriptionMap;
}
```

### ნაბიჯი 3: Georgian Context ქმნება

**კოდი:** `FieldDescriptionService.buildFieldContextString()`

```java
public String buildFieldContextString() {
    Map<String, FieldDescription> descriptions = loadFieldDescriptions();
    StringBuilder context = new StringBuilder();
    context.append("ხელმისაწვდომი ველები და მათი აღწერა:\n\n");

    for (FieldDescription desc : descriptions.values()) {
        context.append("ველი: ").append(desc.getFieldName()).append("\n");
        context.append("ლეიბელი: ").append(desc.getLabelName()).append("\n");
        context.append("აღწერა: ").append(desc.getFieldDesc()).append("\n");
        if (desc.getQuestion() != null && !desc.getQuestion().isEmpty()) {
            context.append("მაგალითი კითხვები: ").append(desc.getQuestion()).append("\n");
        }
        context.append("\n");
    }
    return context.toString();
}
```

### ნაბიჯი 4: LLM-ს გადაცემა

**კოდი:** `RagService.buildContextForQuery()`

```java
public String buildContextForQuery(String userQuery, Integer authorId) {
    List<CarRecord> records = cacheService.getCarRecords(authorId);
    
    // ✅ field_descriptions.json დაკვირვება
    String fieldContext = fieldDescriptionService.buildFieldContextString();
    
    // ✅ მანქანის ჩანაწერები
    String recordsContext = cacheService.serializeRecordsForContext(records);
    
    // ✅ სისტემის ინსტრუქციები (Georgian)
    String systemPrompt = buildSystemPrompt();
    
    // ✅ ყველა კომპონენტი LLM-ს
    return systemPrompt + "\n\n" + fieldContext + "\n\n" + recordsContext;
}
```

---

## 📋 რა ეკრანზე ჩნდება LLM-ს

### სისტემის პრომპტი (Georgian)
```
═══════════════════════════════════════════════════════════════════════════════════
თქვენ ხართ ლიონ აი ასისტენტი - ავტომობილის აუქციონის და ლოგისტიკის ექსპერტი
═══════════════════════════════════════════════════════════════════════════════════

📋 თქვენი მიზანი:
ავტომობილის აუქციონის, ლოგისტიკის და ფინანსური მონაცემების ანალიზი ქართულ ენაზე.

🎯 მოთხოვნები პასუხებისთვის:
1. ენა: ყველა პასუხი ᲛᲮᲝᲚᲝᲓ ქართულ ენაზე
2. სიმართე: მხოლოდ მოცემული მონაცემების გამოყენება
3. სტილი: მოკლე, კონკრეტული და ნათელი პასუხები
...
```

### Field Descriptions (field_descriptions.json)
```
ხელმისაწვდომი ველები და მათი აღწერა:

ველი: usa_geo
ლეიბელი: USA-GEO
აღწერა: აღნიშნავს მანქანის ტრანსპორტირების მიმართულებას, მაგალითად ამერიკიდან საქართველოში...
მაგალითი კითხვები: რომელი მიმართულებით მიდის მანქანა?; მანქანა ამერიკიდან მოდის?...

ველი: manufacturer
ლეიბელი: Manufacture
აღწერა: მანქანის მწარმოებელი კომპანიის დასახელება (მაგალითად: Toyota, BMW, Mercedes-Benz)...
მაგალითი კითხვები: ვინ არის მანქანის მწარმოებელი?; რომელი კომპანიაა მანქანის მწარმოებელი?...

ველი: model
ლეიბელი: Model
აღწერა: მანქანის კონკრეტული მოდელი, რომელიც განსაზღვრავს მის დიზაინს...
მაგალითი კითხვები: რომელი მოდელია მანქანა?; მანქანის მოდელი რა არის?...

[... და ა.შ. ყველა 67 ველი ...]
```

### Car Records (Database)
```
ხელმისაწვდომი მანქანის ჩანაწერები:

ჩანაწერი 1:
  id: 470225
  record_status: current
  modified_timestamp: 2026-05-06 19:28:44
  usa_geo: GEO
  warehouse: All Cargo
  author: GE MOTORS
  author_id: 1748
  buyer_id: Dealer 1748-381
  manufacturer: VOLKSWAGEN
  model: Passat
  year: 2014
  auction_pay: 1285
  f1: 2595
  f2: -2595
  [... და ა.შ. ...]

ჩანაწერი 2:
  id: 470226
  record_status: current
  manufacturer: KIA
  model: Sorento
  year: 2023
  [... და ა.შ. ...]
```

---

## ✅ დადასტურება

### field_descriptions.json გამოიყენება:

1. ✅ **FieldDescriptionService** - ჩატვირთავს JSON-ს
2. ✅ **RagService** - აშენებს Georgian context
3. ✅ **ChatAiService** - გადასცემს LLM-ს
4. ✅ **OpenAI GPT-4o** - იყენებს ველების აღწერებს პასუხის გასაცემად

### მთელი ნაკადი:

```
field_descriptions.json 
    ↓
FieldDescriptionService.loadFieldDescriptions()
    ↓
FieldDescriptionService.buildFieldContextString()
    ↓
RagService.buildContextForQuery()
    ↓
ChatAiService.chat()
    ↓
ChatClient.prompt().system(context).user(query).call()
    ↓
OpenAI GPT-4o
    ↓
Georgian Response ✅
```

---

## 🎯 შედეგი

LLM იღებს:
- ✅ სისტემის ინსტრუქციები (Georgian)
- ✅ ველების აღწერები (field_descriptions.json)
- ✅ მანქანის ჩანაწერები (Database)

და აბრუნებს:
- ✅ ზუსტი პასუხი ქართულად
- ✅ მხოლოდ მოცემული მონაცემების გამოყენებით
- ✅ სწორი ტერმინოლოგიით
