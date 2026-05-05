# JMeter Backend Suite

This folder contains JMeter test plans for the backend API.

## Structure

- `plans/auth/auth-login-smoke.jmx`
  - JWT login checks for platform admin and company admin accounts.
- `plans/auth/auth-login-load.jmx`
  - Repeated login requests for average auth latency and throughput.
- `plans/domain/domain-api-smoke.jmx`
  - CRUD-oriented smoke flow for company, location, inventory, item, user, dashboard, analytics, and company API key endpoints.
- `plans/domain/domain-read-load.jmx`
  - Read-heavy authenticated load plan for companies, locations, inventories, items, users, dashboard, and analytics.
- `plans/ai/ai-api-smoke.jmx`
  - AI scope, analysis, reports, PDF download, and optional item image suggestion flow.
- `plans/ai/ai-analysis-load.jmx`
  - Repeated AI scope, analysis, and report retrieval for average timings.
- `env/local.properties`
  - Local runtime properties loaded with `-q`.

## Before Running

1. Update [local.properties](/Users/gustas/Dev/working/InventoryManagementSystem/backend/performance/jmeter/env/local.properties) with real credentials and host settings.
2. Make sure the backend is running.
   - quick check: `curl -i -s -X POST http://localhost:8080/auth/login -H 'Content-Type: application/json' -d '{"username":"admin","password":"admin"}'`
3. If you want to run the AI plan:
   - configure a valid company API key in the app,
   - point `image_path` to a real image file on disk,
   - set `run_ai_item_suggestion=true` if you want the multipart image sampler enabled.

## Example Commands

```bash
jmeter -n \
  -t backend/performance/jmeter/plans/auth/auth-login-smoke.jmx \
  -q backend/performance/jmeter/env/local.properties \
  -l backend/performance/jmeter/results/auth-login.jtl
```

```bash
jmeter -n \
  -t backend/performance/jmeter/plans/domain/domain-api-smoke.jmx \
  -q backend/performance/jmeter/env/local.properties \
  -l backend/performance/jmeter/results/domain-smoke.jtl \
  -e -o backend/performance/jmeter/results/domain-report
```

```bash
jmeter -n \
  -t backend/performance/jmeter/plans/domain/domain-read-load.jmx \
  -q backend/performance/jmeter/env/local.properties \
  -l backend/performance/jmeter/results/domain-read-load.jtl \
  -e -o backend/performance/jmeter/results/domain-read-load-report
```

```bash
jmeter -n \
  -t backend/performance/jmeter/plans/ai/ai-api-smoke.jmx \
  -q backend/performance/jmeter/env/local.properties \
  -l backend/performance/jmeter/results/ai-smoke.jtl \
  -e -o backend/performance/jmeter/results/ai-report
```

```bash
jmeter -n \
  -t backend/performance/jmeter/plans/ai/ai-analysis-load.jmx \
  -q backend/performance/jmeter/env/local.properties \
  -l backend/performance/jmeter/results/ai-analysis-load.jtl \
  -e -o backend/performance/jmeter/results/ai-analysis-load-report
```

## Notes

- The domain plan creates test data for companies, locations, inventories, items, and users.
- The item flow deletes the created item, but the other entities remain because the API does not expose delete endpoints for them.
- Default seeded credentials in this repo are `admin` / `admin` for the platform admin and `apple.admin` / `admin` for a company admin.
- If the backend is not reachable on the configured `host` and `port`, JMeter will report connection-refused failures for every sampler.
- The AI item suggestion sampler is disabled by default and only runs when `run_ai_item_suggestion=true`.
- The AI analysis endpoint may take noticeably longer than the rest of the suite because it depends on external AI processing.
- The load plans use `auth_*`, `domain_*`, and `ai_*` properties from [local.properties](/Users/gustas/Dev/working/InventoryManagementSystem/backend/performance/jmeter/env/local.properties) for thread count, loops, and ramp-up.
