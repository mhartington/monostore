import { Hono } from "hono";
import { users, generateId, User } from "@monostore/backend-model";
import { auth, session, setUserSession } from "@monostore/backend-middleware";
import {
  validate,
  registerSchema,
  loginSchema,
} from "@monostore/backend-utils";
import { Session } from "hono-sessions";

// Define the Variables type with session included
type Variables = {
  user: User;
  body: User;
  session: Session;
};

const app = new Hono<{ Variables: Variables }>();

// Only apply session middleware to authenticated routes
app.use("/profile", session);
app.use("/logout", session);

// Register new user
app.post("/register", validate(registerSchema), session, (c) => {
  const { username, email, password } = c.get("body");

  // Check if user with email already exists
  if (users.some((u) => u.email === email)) {
    return c.json({ error: "Email already in use" }, 400);
  }

  // Create new user
  const newUser = {
    id: generateId(),
    username,
    email,
    password, // In production, this would be hashed
    role: "customer", // Default role
    created_at: new Date().toISOString(),
  };

  users.push(newUser);

  // Set user session
  setUserSession(c, newUser);

  // Remove password from response
  const { password: _, ...userWithoutPassword } = newUser;

  return c.json(
    {
      message: "User registered successfully",
      user: userWithoutPassword,
    },
    201,
  );
});

// Login user
app.post("/login", validate(loginSchema), session, async (c) => {
  const { email, password } = c.get("body");

  // Find user
  const user = users.find((u) => u.email === email);

  // Check if user exists and password matches
  if (!user || user.password !== password) {
    // In production, would use proper password comparison
    return c.json({ error: "Invalid credentials" }, 401);
  }

  // Clear any existing session by setting user to undefined
  const existingSession = c.get("session");
  if (existingSession) {
    existingSession.set("user", undefined);
  }

  // Set new user session
  setUserSession(c, user);

  // Remove password from response
  const { password: _, ...userWithoutPassword } = user;

  return c.json({
    message: "Login successful",
    user: userWithoutPassword,
  });
});

// Logout user
app.post("/logout", (c) => {
  const session = c.get("session");
  session.set("user", undefined);
  return c.json({ message: "Logged out successfully" });
});

// Get user profile (authenticated)
app.get("/profile", auth, (c) => {
  const authenticatedUser = c.get("user");

  // Find full user details
  const user = users.find((u) => u.id === authenticatedUser.id);

  if (!user) {
    return c.json({ error: "User not found" }, 404);
  }

  // Remove password from response
  const { password: _, ...userWithoutPassword } = user;

  return c.json({ user: userWithoutPassword });
});

export { app as usersRoute };
