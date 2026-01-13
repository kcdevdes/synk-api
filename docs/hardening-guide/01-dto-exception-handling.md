# Phase 1: DTO Layer & Exception Handling

## Overview

Phase 1 establishes the architectural foundation by introducing Data Transfer Objects (DTOs) to decouple the API contract from internal domain entities, and implementing a centralized exception handling framework. This phase is critical as it forms the basis for all subsequent hardening phases.

**Duration:** 2-3 days  
**Priority:** CRITICAL (must complete before Phase 2 & 3)  
**Dependencies:** None  
**Blocking:** Phases 2, 3, 4, 5, 6

---

## Current Problems

### Problem 1: Direct Entity Exposure
- **Issue:** API responses expose internal domain entities directly
- **Risk:** Entity structure changes force API contract changes; tight coupling between backend and frontend
- **Impact:** API is not truly RESTful; violates separation of concerns

### Problem 2: Inconsistent Error Handling
- **Issue:** Controllers catch exceptions and return raw `Map` objects with inconsistent formats
- **Risk:** Clients cannot reliably parse error responses; no standardized error contract
- **Impact:** Poor developer experience; difficult error handling on client side

### Problem 3: No Validation Framework
- **Issue:** Form validation exists but is scattered and not comprehensive
- **Risk:** Invalid data can reach business logic; data integrity issues
- **Impact:** Service layer cluttered with validation code

### Problem 4: Implicit Null Handling
- **Issue:** Service methods return `Optional` or `null` without clear semantics
- **Risk:** Null pointer exceptions in controllers; unclear failure modes
- **Impact:** Defensive programming required everywhere; hard to trace issues

---

## Strategic Improvements

### Improvement 1: Introduce DTO Pattern

**What:** Create separate DTO classes for request and response payloads
- Response DTO (TransactionDTO) - for GET requests
- Create DTO (TransactionCreateDTO) - for POST requests
- Update DTO (TransactionUpdateDTO) - for PUT requests

**Why:** 
- Decouples API contract from database schema
- Allows independent evolution of entity and API
- Clear separation between read and write operations

**Benefits:**
- API stability even when domain model changes
- Easier versioning and backward compatibility
- Clear API contracts for clients

---

### Improvement 2: Centralize Exception Handling

**What:** Create custom exception hierarchy and global exception handler
- Custom exceptions for domain-specific errors (ResourceNotFoundException, InvalidTransactionException)
- GlobalExceptionHandler using Spring's @RestControllerAdvice
- Standardized ErrorResponse format for all errors

**Why:**
- Consistent error format across all endpoints
- Cleaner controller code without try-catch blocks
- Centralized error logging and monitoring

**Benefits:**
- Predictable error responses
- Better error messages for debugging
- Reduced boilerplate in controllers

---

### Improvement 3: Implement Mapper Pattern

**What:** Create a mapper component to convert between entities and DTOs
- Centralized conversion logic
- Validation during mapping
- Easy to add transformations

**Why:**
- Single responsibility: mapping logic not scattered across controllers/services
- Reusable conversion patterns
- Clear audit trail of data transformations

**Benefits:**
- Consistent entity-to-DTO transformation
- Easier to add business logic transformations
- Simplified testing of conversion logic

---

### Improvement 4: Enhance Input Validation

**What:** Add comprehensive validation annotations to DTOs
- Type validation (enum pattern matching)
- Size constraints
- Decimal precision for financial data
- Pattern matching for text fields

**Why:**
- Prevents invalid data from entering business logic early
- Clear validation error messages for clients
- Reduces service layer validation code

**Benefits:**
- Fail fast on invalid input
- Consistent validation rules
- Better user experience with specific error messages

---

## Architectural Changes

### Current Architecture Flow
```
Request → Controller → Service → Repository → Database
  (raw)      (Map)      (null)      (SQL)
```

### Improved Architecture Flow
```
Request → DTO → Mapper → Entity → Service → Repository → Database
          (validation)   (conversion)
```

### Error Handling Flow
```
Exception → GlobalExceptionHandler → StandardErrorResponse → Client
(any layer)    (@RestControllerAdvice)     (JSON)
```

---

## Key Deliverables

### New Components

1. **DTO Layer**
   - Request DTOs with validation annotations
   - Response DTOs with JSON serialization configuration
   - Consistent naming convention (TransactionDTO, TransactionCreateDTO, etc.)

2. **Exception Layer**
   - Custom exception classes for domain errors
   - GlobalExceptionHandler using @RestControllerAdvice
   - Standardized error response format

3. **Mapper Component**
   - Centralized entity-to-DTO conversion
   - Reverse conversion with validation
   - Error handling during mapping

### Modified Components

1. **Controller Layer**
   - Remove direct entity responses
   - Remove manual error handling (try-catch)
   - Use mappers for conversions
   - Return DTOs in all responses

2. **Service Layer**
   - Throw custom exceptions instead of returning null/Optional
   - Remove HTTP-specific code
   - Focus on business logic only

3. **Validation**
   - Move validation from service to DTOs
   - Implement pattern matching for text fields
   - Add decimal constraints for financial data

---

## Data Flow Examples

### Create Transaction Flow

**Before:**
```
POST /api/transactions {raw JSON} 
→ Controller catches exception as Map 
→ Service returns null 
→ Controller returns Map or raw entity
```

**After:**
```
POST /api/transactions {JSON}
→ Spring validates with DTO annotations
→ Mapper converts DTO → Entity
→ Service processes with type safety
→ Service throws custom exception on error
→ GlobalExceptionHandler catches → standardized response
→ Mapper converts Entity → DTO
→ Response returns DTO as JSON
```

### Error Handling Flow

**Before:**
```
Invalid input → Service throws exception → Controller catches it 
→ Returns raw Map with error key → Client must parse manually
```

**After:**
```
Invalid input → DTO validation fails → GlobalExceptionHandler catches 
→ Returns ErrorResponse with field errors → Client gets structured response
```

---

## Impact on Other Layers

### API Contract Impact
- **Breaking Change:** Response format changes from raw entity to DTO
- **Mitigation:** This is internal to API; no consumer impact if no external API exists yet
- **Versioning:** Establish v1 endpoint convention for future compatibility

### Database Layer Impact
- **No Change:** Database schema remains the same
- **Benefit:** Can optimize schema independently of API contract

### Testing Impact
- **New:** Unit tests for mapper logic
- **Enhanced:** Integration tests can rely on consistent response format
- **Benefit:** Easier to mock DTOs; cleaner test setup

---

## Success Metrics

### Code Quality Metrics
- All API responses return DTOs (0% entity exposure)
- 100% of exceptions caught by GlobalExceptionHandler
- No raw Map objects in controller responses
- Zero null returns from service methods

### Developer Experience Metrics
- Error messages are descriptive and actionable
- API contract documented clearly
- Controllers are thin and focused on routing
- Services are clean business logic

### Maintainability Metrics
- DTO changes don't require entity changes
- Validation rules centralized in one place
- Mapper logic reusable across endpoints
- Clear separation between layers

---

## Risk Mitigation

### Risk 1: Breaking API Changes
- **Mitigation:** Document API contract changes; communicate breaking change
- **Timeline:** Implement before external consumers; establish versioning

### Risk 2: Performance Impact
- **Mitigation:** Mapper conversions are negligible; measure with benchmarks if concerned
- **Timeline:** Not expected to be an issue; verify with monitoring

### Risk 3: Over-Engineering
- **Mitigation:** Keep mappers simple; avoid complex transformation logic
- **Timeline:** Review mapper complexity; refactor if exceeds 50 lines per mapper

---

## Implementation Checklist

- [ ] Understand DTO vs Entity separation
- [ ] Design DTO structure for all endpoints
- [ ] Create custom exception classes
- [ ] Implement GlobalExceptionHandler
- [ ] Build mapper component
- [ ] Add validation to DTOs
- [ ] Update controllers to use DTOs
- [ ] Update services to throw exceptions
- [ ] Write tests for mappers
- [ ] Document DTO structure in API docs
- [ ] Verify all endpoints return DTOs
- [ ] Test error responses are standardized

---

## Phase Completion Criteria

✅ **Phase 1 is complete when:**

1. All API endpoints return DTOs (no entities)
2. All error responses follow ErrorResponse format
3. Invalid input triggers DTO validation (not business logic)
4. Custom exceptions thrown from service layer
5. GlobalExceptionHandler handles all exception types
6. Mapper logic is centralized and reusable
7. No raw Map objects in responses
8. Code compiles without errors
9. All tests pass
10. API contract documented clearly

---

## Transition to Next Phase

After Phase 1 completion, the codebase is ready for:
- **Phase 2:** Adding logging without worrying about response format consistency
- **Phase 3:** Implementing security without exception handling conflicts
- **Phase 4:** Configuration management without mixing concerns
- **Phase 5:** Documentation based on stable DTO structure

---

**Phase Status:** 🔴 Not Started  
**Estimated Completion:** 2-3 days  
**Priority:** CRITICAL  
**Next Phase:** [Phase 2: Logging & Observability](./02-logging-observability.md)

