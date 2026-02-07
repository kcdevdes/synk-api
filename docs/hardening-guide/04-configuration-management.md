# Phase 4: Configuration Management

## Overview

Phase 4 implements secure and flexible configuration management by externalizing sensitive credentials and environment-specific settings from the codebase. This phase ensures the application can be deployed across multiple environments without code changes.

**Duration:** 1-2 days  
**Priority:** HIGH  
**Dependencies:** Phase 1 (DTO Layer & Exception Handling)  
**Blocking:** None (can be done before deployment)

---

## Current Configuration Issues

### Issue 1: Hardcoded Credentials Risk
- **Problem:** Database connection settings visible in application.properties
- **Risk:** Credentials could be accidentally committed to version control
- **Impact:** Unauthorized database access if repository is compromised

### Issue 2: Environment-Specific Configuration Duplication
- **Problem:** No clear strategy for dev vs staging vs production settings
- **Risk:** Configuration drift; wrong settings deployed to wrong environment
- **Impact:** Misconfigured environments; security breaches; data loss

### Issue 3: No Configuration Externalization
- **Problem:** Application.properties treated as code artifact
- **Risk:** Configuration changes require code rebuild and redeployment
- **Impact:** Slow configuration updates; inflexible deployment process

### Issue 4: SQL Logging in Production
- **Problem:** spring.jpa.show-sql=true enables SQL logging globally
- **Risk:** Performance degradation; sensitive data in logs
- **Impact:** Compliance violations; reduced performance

### Issue 5: No Feature Flags
- **Problem:** Configuration is static; features cannot be toggled at runtime
- **Risk:** Features go live to all users simultaneously; no gradual rollout
- **Impact:** Risk of bugs affecting all users; no A/B testing capability

---

## Strategic Improvements

### Improvement 1: Externalize Database Credentials

**What:** Move database connection details from properties file to environment variables
- Database URL
- Username
- Password
- Connection pool settings

**Why:**
- Keeps secrets out of version control
- Different credentials per environment
- Easy credential rotation

**Benefits:**
- Secure deployment pipeline
- Environment isolation
- Secrets management flexibility

**Scope:**
- Spring datasource configuration via env vars
- Connection pool size configurable per environment
- Database URL per environment

---

### Improvement 2: Create Environment-Specific Profiles

**What:** Establish separate configuration profiles for each deployment environment
- Development (dev) profile
- Staging (staging) profile
- Production (prod) profile

**Why:**
- Different settings appropriate for each environment
- Clear separation of concerns
- Easy to switch environments

**Benefits:**
- Clear environment management
- Prevents configuration mistakes
- Easy to add new environments

**Scope:**
- application-dev.properties
- application-staging.properties
- application-prod.properties
- application.properties as baseline

---

### Improvement 3: Configure Connection Pooling

**What:** Implement and configure database connection pooling
- Pool size (core, max)
- Connection timeout
- Idle timeout
- Maximum lifetime

**Why:**
- Efficient database connection management
- Prevents connection exhaustion
- Improves performance

**Benefits:**
- Better resource utilization
- Reduced latency
- Better scalability

**Scope:**
- HikariCP connection pool configuration
- Different pool sizes per environment
- Connection validation settings

---

### Improvement 4: Environment Variable Mapping

**What:** Map all configuration values to environment variables
- Standardized naming convention
- Clear documentation
- Type safety

**Why:**
- Enables container deployment
- Works with orchestration platforms
- Follows 12-factor app methodology

**Benefits:**
- Docker/Kubernetes compatible
- Cloud-native deployment
- No configuration files in images

**Scope:**
- Naming convention: APP_MODULE_SETTING
- All sensitive values as env vars
- Non-sensitive defaults in properties

---

### Improvement 5: Logging Configuration Per Environment

**What:** Different logging strategies for each environment
- Development: verbose, console output
- Staging: info-level, file output
- Production: minimal, async, alerting

**Why:**
- Appropriate verbosity per environment
- Performance optimization
- Cost management (log storage)

**Benefits:**
- Development efficiency
- Production performance
- Compliance with log policies

**Scope:**
- Logging level per environment
- Appender selection per environment
- Log retention per environment
- SQL query logging disabled in prod

---

### Improvement 6: Security Configuration Per Environment

**What:** Different security settings for each environment
- Development: CSRF disabled, relaxed CORS
- Production: CSRF enabled, strict CORS

**Why:**
- Testing flexibility in dev
- Production security hardening
- Clear separation

**Benefits:**
- Easier development workflow
- Security in production
- Clear security boundaries

**Scope:**
- CSRF settings per environment
- CORS allowed origins per environment
- HSTS enforcement in production
- SSL/TLS settings per environment

---

### Improvement 7: Feature Toggles/Flags

**What:** Implementation-independent feature flag system
- Enable/disable features via configuration
- Runtime flag evaluation
- No code changes required

**Why:**
- Gradual feature rollout
- A/B testing capability
- Quick rollback if needed

**Benefits:**
- Safer deployments
- Testing in production
- Rapid feature management

**Scope:**
- New features behind flags
- Gradual rollout strategy
- A/B testing framework

---

### Improvement 8: Configuration Documentation

**What:** Clear documentation of all configuration options
- Environment variables reference
- Profile selection guide
- Deployment checklist

**Why:**
- New team members understand configuration
- Reduces deployment errors
- Clear operational procedures

**Benefits:**
- Faster onboarding
- Fewer configuration mistakes
- Better knowledge sharing

**Scope:**
- Configuration properties documented
- Environment variables listed
- Expected values per environment
- Deployment procedures

---

## Configuration Architecture

### Configuration Loading Strategy
```
Startup
    ↓
1. Load application.properties (defaults)
    ↓
2. Load application-{profile}.properties (overrides)
    ↓
3. Load Environment Variables (production secrets)
    ↓
4. ConfigurationProperties beans (type-safe access)
    ↓
Application Ready with Config
```

### Environment Variable Naming Convention
```
APP_MODULE_PROPERTY
├─ APP_ prefix (application namespace)
├─ MODULE_ section (module name)
└─ PROPERTY name (configuration key)

Examples:
- APP_DATASOURCE_URL
- APP_DATASOURCE_USERNAME
- APP_DATASOURCE_PASSWORD
- APP_LOGGING_LEVEL
- APP_CORS_ALLOWED_ORIGINS
- APP_SECURITY_CSRF_ENABLED
- APP_FEATURE_SEARCH_ENABLED
```

### Profile Activation
```
Development:   spring.profiles.active=dev
Staging:       spring.profiles.active=staging
Production:    spring.profiles.active=prod
```

---

## Configuration Categories

### Database Configuration
- Connection URL
- Username
- Password
- Pool size
- Connection timeout
- Validation settings

### Logging Configuration
- Log level
- Appender selection
- Log retention
- File location
- Async settings

### Security Configuration
- CSRF enabled/disabled
- CORS allowed origins
- CORS allowed methods
- HSTS settings
- CSP policy

### Application Configuration
- Server port
- Server context path
- Default time zone
- Feature flags
- Monitoring endpoints

### External Services Configuration
- API keys
- Service endpoints
- Timeouts
- Retry policies

---

## Environment-Specific Settings Matrix

| Setting | Dev | Staging | Prod |
|---------|-----|---------|------|
| Logging Level | DEBUG | INFO | INFO |
| SQL Logging | true | false | false |
| CSRF Enabled | false | true | true |
| CORS Origins | localhost:3000 | staging.domain | api.domain |
| Pool Size | 5 | 10 | 20 |
| Connection Timeout | 10s | 10s | 30s |
| HSTS | disabled | disabled | 1 year |
| Log Retention | 7 days | 30 days | 90 days |

---

## Configuration Sources Priority

### ConfigurationProperties Binding
```
1. Command line arguments (highest)
2. Environment variables (OS level)
3. System properties (Java level)
4. application-{profile}.properties
5. application.properties (lowest)
```

---

## Security Considerations

### Secret Management
- Database credentials from environment
- API keys from environment
- Private keys from mounted volumes
- Never log secrets

### Credential Rotation
- Support credential updates without restart
- Graceful connection pool refresh
- Audit credential changes

### Audit Trail
- Configuration changes logged
- Environment variable usage logged
- Sensitive value masking in logs

---

## Deployment Scenarios

### Local Development
```
Environment: Developer's machine
Profile: dev
Credentials: Local database or embedded
Logging: Console, DEBUG level
Security: CSRF disabled, relaxed CORS
```

### Docker Container Development
```
Environment: Docker
Profile: dev
Credentials: From docker-compose.yml
Logging: File and console, DEBUG level
Security: CSRF disabled, relaxed CORS
```

### Staging Environment
```
Environment: Cloud platform
Profile: staging
Credentials: From secrets management service
Logging: File with rotation, INFO level
Security: CSRF enabled, staging CORS
```

### Production Environment
```
Environment: Kubernetes cluster
Profile: prod
Credentials: From platform secrets manager
Logging: Async file, minimal verbosity
Security: Full security hardening
```

---

## Technology Choices

### Configuration Methods
1. **Spring Boot Properties** - Simple, file-based
2. **Environment Variables** - Cloud-native, flexible
3. **ConfigurationProperties** - Type-safe, bean-based
4. **Config Server** - Centralized, dynamic (future)

### Secrets Management
1. **Environment Variables** - MVP approach
2. **Kubernetes Secrets** - If using K8s
3. **Cloud Provider Secrets** (AWS/Azure/GCP) - Enterprise approach
4. **HashiCorp Vault** - Advanced security

---

## Migration Path

### Phase 0: Current State
- All config in application.properties
- Credentials in file
- Single environment

### Phase 1: Profiles
- application-dev.properties
- application-prod.properties
- Profile selection at startup

### Phase 2: Environment Variables
- Database credentials from env vars
- Common settings from env vars
- Fallback to properties

### Phase 3: Configuration Properties Beans
- Type-safe access
- Validation
- Documentation

### Phase 4: Secrets Manager
- External secrets service
- Credential rotation
- Audit trails

---

## Implementation Checklist

- [x] Identify all configuration values
- [x] Create environment-specific profiles
- [x] Define environment variable naming convention
- [x] Create ConfigurationProperties classes
- [x] Document all configuration options
- [x] Create deployment checklist
- [x] Plan secrets management approach
- [x] Create environment variable examples
- [x] Set up CI/CD with profile selection
- [x] Test each environment configuration

---

## Phase Completion Criteria

✅ **Phase 4 is complete when:**

1. Database credentials not in code
2. Environment-specific profiles created
3. Environment variables mapped for all secrets
4. ConfigurationProperties beans implemented
5. Configuration documented
6. Deployment checklist created
7. Each environment tested independently
8. No credentials in logs
9. Configuration load order verified
10. All environments deployable without code changes

---

## Documentation Requirements

### Configuration Reference
- List of all configurable values
- Default values per environment
- Type and format for each value
- When it's used in code

### Deployment Guide
- Step-by-step for each environment
- Environment variables to set
- Configuration file requirements
- Verification steps

### Troubleshooting Guide
- Common configuration errors
- How to verify active profile
- How to check loaded configuration
- Environment variable debugging

---

## Transition to Next Phases

After Phase 4 completion:
- **Phase 5:** API documentation can reference configuration
- **Phase 6:** Database optimization uses connection pool settings
- **Phase 8:** Rate limiting uses configuration for limits

---

**Phase Status:** 🔴 Not Started  
**Estimated Completion:** 1-2 days  
**Priority:** HIGH  
**Previous Phase:** [Phase 3: Security Hardening](./03-security-hardening.md)  
**Next Phase:** [Phase 5: API Documentation](./05-api-documentation.md)
