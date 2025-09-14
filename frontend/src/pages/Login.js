import { useState } from "react";
import { useNavigate } from "react-router-dom";
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
        <div className="flex items-center justify-center h-screen bg-gray-100">
            <form
                onSubmit={handleSubmit}
                className="bg-white p-6 rounded shadow-md w-80"
            >
                <h2 className="text-xl font-bold mb-4">Login</h2>
                <input
                    name="email"
                    type="email"
                    placeholder="Email"
                    value={form.email}
                    onChange={handleChange}
                    className="border p-2 w-full mb-2 rounded"
                />
                <input
                    name="password"
                    type="password"
                    placeholder="Password"
                    value={form.password}
                    onChange={handleChange}
                    className="border p-2 w-full mb-4 rounded"
                />
                <button className="bg-green-500 text-white p-2 w-full rounded">
                    Login
                </button>
                {message && (
                    <p className="mt-2 text-sm text-red-500">{message}</p>
                )}
            </form>
        </div>
    );
}
