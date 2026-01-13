# Phase 3: Security Hardening

## Overview

Phase 3 implements comprehensive security measures to protect the API from common vulnerabilities including SQL injection, XSS attacks, CSRF, insecure headers, and unauthorized access. This phase establishes a security-first foundation.

**Duration:** 2-3 days  
**Priority:** CRITICAL (must complete before production)  
**Dependencies:** Phase 1 (DTO Layer & Exception Handling)  
**Blocking:** None (can be integrated in parallel with other phases)

---

## Current Security Gaps

### Gap 1: No Input Validation Framework
- **Issue:** Basic form validation exists but lacks depth; no centralized validation policy
- **Risk:** Malicious or malformed input reaches business logic; potential for injection attacks
- **Impact:** Data corruption; system abuse; regulatory violations

### Gap 2: SQL Injection Vulnerability
- **Issue:** Search and filter methods load all data into memory and filter in-application
- **Risk:** If refactored to use JPQL/SQL, parameterization might be missing
- **Impact:** Unauthorized data access; data modification; system compromise

### Gap 3: No XSS Protection
- **Issue:** No Content Security Policy; no output escaping; no input sanitization
- **Risk:** Attackers inject malicious scripts through form fields
- **Impact:** User session hijacking; credential theft; malware distribution

### Gap 4: Missing Security Headers
- **Issue:** No HSTS, CSP, X-Frame-Options, X-Content-Type-Options headers
- **Risk:** Attackers exploit browser vulnerabilities; clickjacking attacks
- **Impact:** Session hijacking; MITM attacks; data theft

### Gap 5: CORS Misconfiguration
- **Issue:** No CORS policy; potential for overly permissive access
- **Risk:** Unauthorized cross-origin requests; data exposure
- **Impact:** Data breach; unauthorized access

### Gap 6: No CSRF Protection
- **Issue:** No CSRF tokens; state-changing operations vulnerable
- **Risk:** Attackers trick users into modifying data
- **Impact:** Unauthorized data modifications; account compromise

### Gap 7: Credential Exposure
- **Issue:** Database credentials in application.properties file (empty but in code)
- **Risk:** Credentials could be committed to version control
- **Impact:** Unauthorized database access

---

## Threat Model

### Attack Vector 1: SQL Injection
**Attacker Goal:** Execute arbitrary SQL queries
**Attack Method:** Craft malicious search query with SQL syntax
**Current Mitigation:** In-memory filtering (not vulnerable)
**Future Risk:** If moved to database queries, parameterization required

---

### Attack Vector 2: Cross-Site Scripting (XSS)
**Attacker Goal:** Inject malicious JavaScript into responses
**Attack Method:** Provide `<script>` or event handler in merchant name
**Current Mitigation:** None
**Potential Impact:** Session hijacking, credential theft

---

### Attack Vector 3: Cross-Site Request Forgery (CSRF)
**Attacker Goal:** Perform unauthorized actions on behalf of user
**Attack Method:** Trick user into clicking link that triggers API request
**Current Mitigation:** None
**Potential Impact:** Unauthorized transactions, data modification

---

### Attack Vector 4: Clickjacking
**Attacker Goal:** Trick user into clicking hidden frame
**Attack Method:** Embed API endpoint in iframe
**Current Mitigation:** None
**Potential Impact:** Unauthorized actions, data theft

---

### Attack Vector 5: Header-Based Attacks
**Attacker Goal:** Exploit browser security features
**Attack Method:** Remove/modify security headers
**Current Mitigation:** None
**Potential Impact:** MITM attacks, data exposure

---

## Strategic Improvements

### Improvement 1: Implement Input Validation Strategy

**What:** Comprehensive validation at DTO layer with multiple checks
- Type validation (enum values only)
- Size constraints (prevent buffer overflow)
- Pattern matching (whitelist allowed characters)
- Format validation (decimal precision for financial data)
- Range validation (amount must be positive)

**Why:**
- Defense-in-depth: validation at entry point
- Fail fast on invalid input
- Clear error messages for clients

**Benefits:**
- Prevents malicious input early
- Reduces business logic complexity
- Better user experience

**Scope:**
- Type field: must be INCOME, EXPENSE, or TRANSFER
- Amount field: positive decimal with 2 decimal places
- Merchant field: alphanumeric, spaces, hyphens, dots, apostrophes only
- Description field: no HTML tags or script content

---

### Improvement 2: Prevent SQL Injection

**What:** Use parameterized queries and avoid dynamic SQL
- Refactor search methods to use JPQL with parameter binding
- Refactor filter methods to use type-safe queries
- Never concatenate strings into SQL

**Why:**
- Parameter binding prevents SQL injection
- Type-safe queries reduce errors
- ORM handles escaping automatically

**Benefits:**
- Complete protection against SQL injection
- Maintainable query code
- Better performance with prepared statements

**Scope:**
- Search by merchant name using JPQL LIKE with parameters
- Filter by transaction type using type-safe methods
- All future queries use parameterized approach

---

### Improvement 3: Protect Against XSS Attacks

**What:** Multi-layer XSS protection strategy
- Input sanitization (remove dangerous characters)
- Output encoding (escape special characters)
- Content Security Policy (CSP) headers
- X-XSS-Protection header for browser compatibility

**Why:**
- Defense-in-depth prevents XSS
- CSP restricts script execution
- Multiple layers catch different attack vectors

**Benefits:**
- Comprehensive XSS protection
- Backward compatible with older browsers
- Clear security policy for clients

**Scope:**
- Block script tags and event handlers in input
- Escape HTML special characters in output
- Set CSP headers to prevent inline scripts
- X-Frame-Options to prevent clickjacking

---

### Improvement 4: Add Security Headers

**What:** Configure response headers for browser security
- Strict-Transport-Security (HSTS) for HTTPS
- Content-Security-Policy (CSP) for script control
- X-Frame-Options to prevent clickjacking
- X-Content-Type-Options to prevent MIME sniffing
- Referrer-Policy for privacy
- Permissions-Policy to disable features

**Why:**
- Headers instruct browsers on security policy
- Prevents multiple attack vectors
- Zero-effort protection for clients

**Benefits:**
- Browser enforces security policies
- No client-side code changes needed
- Defense against multiple attacks

**Scope:**
- HSTS: enforce HTTPS for 1 year
- CSP: restrict scripts to same-origin
- X-Frame-Options: DENY (prevent embedding)
- X-Content-Type-Options: nosniff (prevent MIME sniffing)
- Referrer-Policy: strict-origin-when-cross-origin

---

### Improvement 5: Configure CORS Policy

**What:** Implement restrictive CORS configuration
- Whitelist allowed origins
- Restrict allowed HTTP methods
- Specify allowed headers
- Control credentials behavior

**Why:**
- Prevents unauthorized cross-origin requests
- Restricts access to known clients
- Explicit allowlist reduces attack surface

**Benefits:**
- Only trusted origins can access API
- Clear security boundary
- Easier to audit and maintain

**Scope:**
- Allow only localhost:3000 and localhost:8080 in development
- Production configuration from environment variables
- Methods: GET, POST, PUT, DELETE, OPTIONS
- Credentials: allowed with same-site policy

---

### Improvement 6: Implement CSRF Protection

**What:** Use Spring Security's CSRF token mechanism
- Generate CSRF tokens for state-changing operations
- Validate tokens in requests
- Store tokens in secure cookies

**Why:**
- Prevents forged requests
- Browser enforces same-site policy
- Token validation confirms user intent

**Benefits:**
- Protection against CSRF attacks
- No user-facing complexity
- Automatic token management

**Scope:**
- Enable for POST, PUT, DELETE operations
- Disable for stateless API if using JWT (future)
- Token in header or request body

---

### Improvement 7: Establish Input Sanitization

**What:** Create utility functions for input sanitization
- HTML entity encoding
- SQL injection pattern detection
- Script tag removal
- XSS pattern detection

**Why:**
- Defense-in-depth measure
- Catches edge cases
- Clear sanitization logic

**Benefits:**
- Additional security layer
- Explicit data cleaning
- Reusable across components

**Scope:**
- Sanitize text fields before storage
- Detect and reject suspicious patterns
- Log suspicious input attempts
- HTML encode output data

---

## Security Architecture

### Validation Architecture
```
Request (JSON)
    ↓
DTO Annotations (Spring Validation)
    ├─→ Type Check (Enum pattern)
    ├─→ Size Check (Length limits)
    ├─→ Pattern Check (Whitelist allowed chars)
    ├─→ Range Check (Amount > 0)
    └─→ Format Check (Decimal precision)
    ↓
Validation Result (Pass/Fail)
    ├─→ Pass: Continue to Service
    └─→ Fail: GlobalExceptionHandler → Error Response
```

### Query Execution Architecture
```
User Input → Parameterized Query
    ↓
JPQL with Parameter Binding
    ↓
Hibernate Parameter Binding
    ↓
Database Driver Escaping
    ↓
Safe SQL Execution
```

### Security Headers Architecture
```
HTTP Response
    ↓
SecurityConfig (Spring Security)
    ├─→ HSTS Header
    ├─→ CSP Header
    ├─→ X-Frame-Options
    ├─→ X-Content-Type-Options
    ├─→ Referrer-Policy
    └─→ Permissions-Policy
    ↓
Response Sent to Client
```

---

## OWASP Top 10 Coverage

This phase addresses:

| OWASP | Vulnerability | Mitigation |
|-------|----------------|-----------|
| A01:2021 | Broken Access Control | CORS configuration, Origin validation |
| A03:2021 | Injection (SQL) | Parameterized queries, Input validation |
| A04:2021 | Insecure Design | Security-first design, Threat modeling |
| A05:2021 | Security Misconfiguration | Security headers, CORS policy |
| A07:2021 | Cross-Site Scripting (XSS) | Input sanitization, CSP headers |
| A08:2021 | Software Integrity | Header validation, HSTS |
| A09:2021 | Logging & Monitoring | Security event logging |

---

## Configuration Strategy

### Development Environment
- CSRF disabled for easier testing
- CORS allows localhost:3000 and localhost:8080
- CSP policy relaxed for debugging
- HTTP allowed (no HSTS enforcement)

### Production Environment
- CSRF enabled
- CORS only allows production domain
- CSP strict (no inline scripts)
- HTTPS enforced (HSTS 1 year)
- All security headers enabled

---

## Integration Points

### With Phase 1 (DTO & Exception Handling)
- DTO validation prevents invalid input
- GlobalExceptionHandler returns validation errors
- Mapper validates during conversion

### With Phase 2 (Logging & Observability)
- Log suspicious input attempts
- Audit security events
- Track failed validations

### With Phase 4 (Configuration Management)
- CORS allowed origins from environment
- Security header values from configuration
- Credential management

### With Phase 6 (Database Optimization)
- Parameterized queries for SQL injection prevention
- Query performance monitoring

---

## Security Testing Strategy

### Input Validation Testing
- Test with enum values outside allowed range
- Test with special characters in merchant name
- Test with HTML tags in description
- Test with SQL injection patterns
- Test with XSS payloads

### Header Verification Testing
- Verify all security headers present
- Verify header values correct
- Test on multiple browsers

### CORS Testing
- Test allowed origins
- Test disallowed origins
- Test allowed methods
- Test credential handling

### SQL Injection Testing
- Test search with SQL syntax
- Test filter with UNION statements
- Verify parameterized queries used

---

## Risk Mitigation

### Risk 1: Performance Impact
- **Concern:** Input validation might slow requests
- **Mitigation:** Validation is lightweight; measure with benchmarks
- **Timeline:** Monitor in testing phase

### Risk 2: Breaking Changes
- **Concern:** Strict input validation might reject valid data
- **Mitigation:** Review existing data; adjust patterns if needed
- **Timeline:** Test with production-like data

### Risk 3: Deployment Complexity
- **Concern:** Security changes might require infrastructure updates
- **Mitigation:** Configuration externalization; rolling deployment
- **Timeline:** Plan with DevOps team

---

## Compliance Considerations

### PCI DSS (if handling payment data)
- Secure transmission (HTTPS)
- Input validation
- SQL injection prevention

### GDPR (if storing user data)
- Data protection in transit
- Audit trails for access
- Privacy headers (Referrer-Policy)

### SOC 2 (for compliance audits)
- Security controls documented
- Audit trails maintained
- Configuration management

---

## Implementation Checklist

- [ ] Understand OWASP Top 10
- [ ] Review threat model
- [ ] Design validation rules for each field
- [ ] Plan parameterized query strategy
- [ ] Design input sanitization approach
- [ ] Select security header strategy
- [ ] Plan CORS configuration
- [ ] Design CSRF token flow
- [ ] Create security testing plan
- [ ] Plan environment-specific configuration

---

## Phase Completion Criteria

✅ **Phase 3 is complete when:**

1. All input validated at DTO layer
2. No invalid input reaches business logic
3. All queries use parameterization
4. No SQL injection vulnerabilities found
5. All security headers present in responses
6. XSS attack vectors blocked
7. CSRF protection enabled
8. CORS policy restrictive
9. Security tests pass
10. Code compiles without errors
11. OWASP Top 10 risks mitigated

---

## Transition to Next Phases

After Phase 3 completion:
- **Phase 2:** Can implement security event logging
- **Phase 4:** Configuration management for CORS and headers
- **Phase 5:** API documentation includes security headers
- **Phase 6:** Queries use parameterized approach

---

**Phase Status:** 🔴 Not Started  
**Estimated Completion:** 2-3 days  
**Priority:** CRITICAL  
**Previous Phase:** [Phase 1: DTO Layer & Exception Handling](./01-dto-exception-handling.md)  
**Next Phase:** [Phase 4: Configuration Management](./04-configuration-management.md)

