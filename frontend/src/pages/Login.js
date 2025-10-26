import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../utils/api";

// helper: decode JWT
function parseJwt(token) {
  try {
    return JSON.parse(atob(token.split(".")[1]));
  } catch (e) {
    return null;
  }
}

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post("/auth/login", form, {
        headers: { "Content-Type": "application/json" },
      });

      const token = res.data;
      localStorage.setItem("token", token);
      localStorage.setItem("email", form.email);

      // decode token to check role
      const decoded = parseJwt(token);
      console.log("Decoded token:", decoded);

      // your backend sets roles as authorities inside JWT
      const role =
        decoded?.roles?.[0] || // backend puts roles here
        decoded?.authorities?.[0] || // fallback if using Spring authorities
        decoded?.role; // fallback if single role

      console.log("Role:", role);
      let redirectPath = "/student"; // default
      if (role === "ROLE_TEACHER") redirectPath = "/teacher";
      else if (role === "ROLE_ADMIN") redirectPath = "/admin";

      setMessage("Login successful!");
      navigate(redirectPath);
    } catch (err) {
      setMessage("Invalid credentials");
    }
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <form
        onSubmit={handleSubmit}
        className="bg-white p-8 rounded-lg shadow-md w-96"
      >
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">
          Welcome Back
        </h2>
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Email Address
          </label>
          <input
            name="email"
            type="email"
            placeholder="Enter your email"
            value={form.email}
            onChange={handleChange}
            className="border border-gray-300 p-3 w-full rounded-md focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
          />
        </div>
        <div className="mb-6">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Password
          </label>
          <input
            name="password"
            type="password"
            placeholder="Enter your password"
            value={form.password}
            onChange={handleChange}
            className="border border-gray-300 p-3 w-full rounded-md focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
          />
        </div>
        <button className="bg-green-500 hover:bg-green-600 text-white font-medium p-3 w-full rounded-md transition duration-200 ease-in-out transform hover:scale-105">
          Login
        </button>
        {message && (
          <p className="mt-4 text-sm text-center text-red-500">{message}</p>
        )}

        {/* Register Link */}
        <div className="mt-6 text-center">
          <p className="text-sm text-gray-600">
            Don't have an account?{" "}
            <Link
              to="/register"
              className="text-green-500 hover:text-green-600 font-medium hover:underline"
            >
              Sign up here
            </Link>
          </p>
        </div>
      </form>
    </div>
  );
}
