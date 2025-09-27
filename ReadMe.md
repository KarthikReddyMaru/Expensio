# Cashigo 💰
*Spend it but Track it*

A smart **personal expense tracker** designed to help you **budget, monitor, and control** your spending with ease.

## 🚀 Features

* **Categories & Subcategories**
    * Built-in **system categories** (e.g., Food, Transport, Entertainment).
    * Create your own categories and subcategories.
    * Create user-specific subcategories under system categories.
    * Prevents duplicates with uniqueness constraints.

* **Budgets & Cycles**
    * Define **budget rules** (`BudgetDefinition`) and create **budget cycles** (`BudgetCycle`).
    * Supports **multiple cycles per budget** while enforcing **only one active cycle** at a time.
    * **Budget refreshes:** create budgets scoped weekly or monthly (and refresh cycles automatically).
    * Tracks `amountSpent` for each cycle and preserves history (previous cycles remain archived).

* **Transactions**
    * Linked to budget cycles and categories.
    * Optimized retrieval using fetch joins when needed.

* **Recurring Transactions**
    * Define recurring payments (e.g., subscriptions, rent).
    * Enforces **future-dated rules** for recurring schedules.
