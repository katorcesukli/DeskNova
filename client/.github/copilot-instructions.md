# DeskNova Client - AI Coding Agent Instructions

## Project Overview
DeskNova is a **role-based ticket management SPA** built with React 19, TypeScript, Vite, and Tailwind CSS. It serves three distinct user roles: **ADMIN**, **AGENT**, and **CLIENT**, each with separate dashboards (`/admin`, `/agent`, `/client`). The backend API runs at `http://localhost:8080/api`.

## Architecture & Data Flow

### Authentication Flow
- **Auth State Management**: Uses React Context (`AuthContext.tsx`) — stores User object and JWT token in React state + localStorage
- **JWT Persistence**: Token and user data cached in localStorage (`jwtToken`, `loggedUser`) for persistence across sessions
- **Role-Based Routing**: `PrivateRoute` wrapper component checks user role and redirects (App.tsx lines 11-18) — role validation gates pages
- **Token Usage**: All API requests include `Authorization: Bearer ${token}` header (example: AdminPage.tsx line 51)
- **Redirect Logic**: `redirectByRole()` function routes users post-login based on uppercase role string (AuthContext.tsx line 53)

### Component Organization
```
src/components/ui/        → Reusable UI components (Button, Modal, Card, Input, Navbar)
src/components/           → Feature components (BarChart, Pagination)
component/modal/          → Modal dialogs for forms (ticket-modal, user-modal)
src/pages/               → Route-level pages with business logic (AdminPage, AgentPage, ClientPage)
src/context/             → State management (AuthContext)
src/lib/                 → Utility functions and helpers (badges.ts, cn.ts, utils.ts)
types/                   → Centralized TypeScript interfaces (User, Ticket, TicketDetail, etc.)
```

### API Integration Pattern
- **Base URL**: `http://localhost:8080/api`
- **Endpoints Used**:
  - `/auth/login` — POST, returns `{token, firstName, lastName, role, email, id}`
  - `/auth/register` — POST, user signup
  - `/api/user` — GET all users (admin only), POST/PATCH user management
  - `/api/ticket` — GET tickets (paginated via `.content`), PATCH to update
  - `/api/metric/admin` — GET admin metrics (dashboard stats)
- **Response Structure**: Paginated responses use `.content` array (AdminPage.tsx line 75: `data.content || []`)
- **Error Handling**: All fetches wrapped in try-catch; error logging to console mandatory

## Key Patterns & Conventions

### Styling & Component Library
- **Tailwind CSS with Vite integration** (`@tailwindcss/vite` in vite.config.ts)
- **Custom Button Component**: Uses `cn()` utility to merge base tailwind classes with variants (Button.tsx line 12) — variants: `default|primary|danger|ghost`
- **Badge Utilities**: `getStatusClasses()` and `getPriorityClasses()` return tailwind classes for status badges (badges.ts) — status values: `OPEN|IN_PROGRESS|RESOLVED|CLOSED`; priority: `LOW|MEDIUM|HIGH`
- **Modal Component**: Custom glassmorphism design with backdrop blur (Modal.tsx) — not Radix Dialog, uses custom implementation
- **Class Merging**: Always use `cn()` from `src/lib/cn.ts` to safely merge conflicting Tailwind classes

### Form Handling & Validation
- **Inline Validation**: Client-side validation in form handlers (LoginPage.tsx lines 23-28 shows email regex pattern)
- **Loading States**: Use component state for `isLoading` boolean, disable form inputs during submission
- **Error Display**: Store single `error` string in state, display above form; clear on new submission

### Pagination
- **Items Per Page**: Admin pages use 5 items per page (`AdminPage.tsx line 46`)
- **Pagination Component**: `<Pagination />` handled separately; manages page state and display range
- **Filtered Data**: Apply search/filter filters BEFORE pagination calculations

### Modal Pattern
- **State Management**: Keep `showXModal` boolean + `editingX` object in parent state
- **Modal Lifecycle**: Pass `onClose` callback to Modal, trigger `setShowXModal(false)` inside
- **Form vs Display**: Modals contain form input (Input components) or display data

### Data Types
- **User**: `{id?, firstName, lastName, email, role, updatedAt?}` — role is uppercase string (ADMIN|AGENT|CLIENT)
- **Ticket/TicketDetail**: Has `client: Person`, optional `agent: Person` (Person = `{id, fullName}`)
- **Priority/Status Objects**: May be nested objects (`{ name: string }`) or simple strings
- **API Response Pattern**: Tickets endpoint returns `{content: TicketDetail[]}` wrapper

## Development Workflow

### Commands
```bash
npm run dev      # Start Vite dev server with HMR (runs on http://localhost:5173 by default)
npm run build    # TypeScript check (tsc -b) + Vite build → dist/
npm run lint     # ESLint check (includes React refresh + hooks rules)
npm run preview  # Preview production build locally
```

### TypeScript Requirements
- **Strict Mode Enabled** (`tsconfig.json`): No `any`, explicit function return types required
- **Type Imports**: Use `type { X }` syntax for type-only imports (AdminPage.tsx line 6)
- **Component Props**: Define interface extending `React.HTMLAttributes` for HTML element props (Button.tsx line 5)
- **Error Typing**: Always check `err instanceof Error` before accessing `.message` (LoginPage.tsx line 62)

### ESLint Rules
- React Refresh fast refresh plugin active — **NEVER export components as non-default or use const assignment** (use `function Component()` or `export default const`)
- React Hooks ESLint rules enforced
- No console warnings should ship — all debug logs are errors in lint

## Integration Points & Dependencies

### External Libraries Used
- `react-router-dom@7.13.1` — Routing (BrowserRouter, Routes, Route, Navigate, useNavigate)
- `@radix-ui/*` — Dialog primitives (dismiss layer, portal) but NOT used in custom Modal
- `lucide-react@0.577.0` — Icons (import specific icon components as needed)
- `class-variance-authority` — CSS class variant utility (installed but check usage)
- `tailwind-merge@1.12.0` — Used in `cn()` utility to prevent Tailwind conflicts

### Backend Communication
- All requests to backend must be authenticated with JWT token in header
- Backend expects `Content-Type: application/json` for POST/PATCH
- Backend response on auth failure returns error message in `data.message`
- **Critical**: Backend runs on port 8080; verify it's running before testing (localhost:8080/api is reachable)

## Code Style & Best Practices

### Component Structure
1. Imports (group: React → libraries → local components → types)
2. Types/Interfaces (if not in types/ folder)
3. Constants (API_URL, default values)
4. Component function declaration
5. State management (useState hooks)
6. Context usage (useContext)
7. Side effects (useEffect)
8. Event handlers
9. Render JSX

### Naming Conventions
- Pages: Pascal case, suffix `Page` (LoginPage, AdminPage)
- Components: Pascal case, no suffix
- Utilities: camelCase (getStatusClasses, cn)
- State variables: camelCase (showUserModal, editingUser)
- Event handlers: camelCase, prefix `on` or `handle` (handleSubmit, openEditUser)

### Common Pitfalls to Avoid
- ❌ Storing sensitive data (JWT) in sessionStorage — use localStorage for persistence
- ❌ Forgetting `Authorization` header in API calls — always include Bearer token
- ❌ Using direct HTML `<div>` overlays for modals instead of custom Modal component
- ❌ Mixing direct `fetch` calls with error states — always set loading/error states
- ❌ Returning different component types from PrivateRoute — use conditional rendering
- ❌ Inconsistent pagination item counts — define `itemsPerPage` constant at top of page

## Testing & Debugging

### Manual Testing Checklist
1. Verify backend API is running on localhost:8080
2. Clear localStorage before testing auth flow (`localStorage.clear()` in console)
3. Test PrivateRoute by logging in with different roles, attempting invalid route access
4. Check network tab in DevTools — all JWT-protected requests must have `Authorization` header
5. Verify localStorage contains `jwtToken` and `loggedUser` after login

### Debug Approach
- Use React DevTools to inspect Context values and component state
- Check browser console for async fetch errors with full stack
- Network tab: verify response status codes and token header presence
