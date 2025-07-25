import { Session, sessionMiddleware, CookieStore } from "hono-sessions";
import { User } from "@monostore/backend-model";
import { createMiddleware } from "hono/factory";

// Define session data types
export type SessionDataTypes = {
  user: Omit<User, "password">;
};

// Secret key (in production, this would be in environment variables)
const SESSION_SECRET = "0a732767d5ee374d4a3478077f84a009863";

// Create cookie stor5173e
const store = new CookieStore();

// Session middleware configuration
export const session = sessionMiddleware({
  store,
  encryptionKey: SESSION_SECRET,
  expireAfterSeconds: 24 * 60 * 60, // 1 day
  cookieOptions: {
    sameSite: "lax", // Allow cross-site cookies
    path: "/",
    httpOnly: true,
    secure: false, // Required for SameSite=None
    // domain: 'localhost'  // Explicitly set domain
  },
});

// Auth middleware
export const auth = createMiddleware(async (c, next) => {
  const cookies = c.req.header("cookie");

  const session = c.get("session");

  const user = session.get("user");

  if (!user) {
    return c.json({ error: "Unauthorized - Please login" }, 401);
  }

  c.set("user", user);
  return next();
});

// Admin middleware (checks if user has admin role)
export const adminOnly = createMiddleware(async (c, next) => {
  const user = c.get("user");

  if (!user || user.role !== "admin") {
    return c.json({ error: "Forbidden - Admin access required" }, 403);
  }

  return next();
});

// Helper function to set user session
export function setUserSession(c: any, user: User) {
  const { password: _, ...userWithoutPassword } = user;
  const sessionData = c.get("session") as Session<SessionDataTypes>;
  sessionData.set("user", userWithoutPassword);
}
