# API documentation

Base URLs (local): consumer `8081`, provider `8082`, order `8083`.

All protected routes need `Authorization: Bearer <JWT>`.

## Consumer service

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/consumers/register` | no | Register organization |
| POST | `/api/auth/login` | no | Login |
| GET/PUT | `/api/consumers/profile` | yes | Profile |
| POST | `/api/meal-requests` | yes | Create request |
| GET | `/api/meal-requests` | yes | My requests |
| GET | `/api/meal-requests/open` | no | Open requests for providers |
| GET | `/api/meal-requests/{id}` | no | Request details |
| PUT | `/api/meal-requests/{id}` | yes | Update |
| DELETE | `/api/meal-requests/{id}` | yes | Cancel |
| PUT | `/api/meal-requests/{id}/status` | no | Internal status update |

Meal request statuses: `PENDING`, `QUOTATION_RECEIVED`, `QUOTATION_ACCEPTED`, `CANCELLED`.

## Provider service

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/providers/register` | no | Register |
| POST | `/api/providers/login` | no | Login |
| GET/PUT | `/api/providers/profile` | yes | Profile / capacity / availability |
| GET | `/api/providers/{id}` | no | Public provider (for maps) |
| POST/GET/PUT/DELETE | `/api/food-items` | yes | Menu |
| GET | `/api/quotations/available` | yes | Open meal requests |
| POST | `/api/quotations` | yes | Submit quote |
| GET | `/api/quotations/mine` | yes | My quotes |
| GET | `/api/quotations/meal-request/{id}` | no | Quotes for a request |
| GET | `/api/quotations/{id}` | no | Quote details |
| PUT | `/api/quotations/{id}/status` | no | Internal status update |

## Order service

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/api/orders/accept-quotation` | consumer | Create order |
| GET | `/api/orders` | yes | Role-scoped list |
| GET | `/api/orders/{id}` | yes | Details |
| PUT | `/api/orders/{id}/status` | yes | Update order status |
| GET | `/api/orders/{id}/track` | yes | GPS + distance + ETA |
| POST | `/api/delivery/register` | no | Rider register |
| POST | `/api/delivery/login` | no | Rider login |
| GET | `/api/delivery/available` | yes | AVAILABLE riders |
| GET | `/api/delivery/requests` | yes | Open delivery jobs |
| POST | `/api/delivery/{id}/accept` | rider | Accept job |
| PUT | `/api/delivery/{id}/location` | rider | GPS update |
| PUT | `/api/delivery/{id}/status` | rider | PICKED_UP / ON_THE_WAY / DELIVERED |
| GET | `/api/notifications` | yes | Inbox |
| PUT | `/api/notifications/{id}/read` | yes | Mark read |

Order statuses: `CREATED`, `CONFIRMED`, `PREPARING`, `READY_FOR_PICKUP`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`.
