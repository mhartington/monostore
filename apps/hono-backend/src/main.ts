import { serve } from "@hono/node-server";
import { Hono } from "hono";
import { cors } from "hono/cors";
import { logger } from "hono/logger";
import { HTTPException } from "hono/http-exception";

import {
  productsRoute,
  usersRoute,
  cartRoute,
  ordersRoute,
} from "@monostore/backend-routes";

const app = new Hono();

app.use("*", logger());
app.use(
  "*",
  cors({
    origin: ["http://localhost:4201", "http://localhost:4200"],
    allowMethods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    allowHeaders: ["Content-Type", "Cookie", "Set-Cookie", "Authorization"],
    exposeHeaders: ["Set-Cookie", "Authorization"],
    maxAge: 600,
    credentials: true,
  }),
);

app.get("/", (c) => {
  return c.json({
    message: "E-commerce API is running",
    version: "1.0.0",
    endpoints: {
      products: "/api/products",
      users: "/api/users",
      cart: "/api/cart",
      orders: "/api/orders",
    },
  });
});

app.route("/api/users", usersRoute);
app.route("/api/products", productsRoute);
app.route("/api/cart", cartRoute);
app.route("/api/orders", ordersRoute);

app.onError((err, c) => {
  const status = (err as HTTPException).status || 500;
  const message = status === 500 ? "Internal Server Error" : err.message;
  return c.json({ error: message }, status);
});

app.notFound((c) => {
  return c.json({ error: "Not Found" }, 404);
});

const port = 3000;

console.log(`Server starting on port ${port}...`);
serve({
  fetch: app.fetch,
  port,
});

console.log(`Server running at http://localhost:${port}`);
