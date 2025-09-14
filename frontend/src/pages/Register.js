import { useState } from "react";
import api from "../utils/api";

export default function Register() {
    const [form, setForm] = useState({
        fullName: "",
        email: "",
        password: "",
        role: "ROLE_STUDENT"
    });
    const [message, setMessage] = useState("");

    const handleChange = (e) =>
        setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const body = {
                fullName: form.fullName,
                email: form.email,
                password: form.password,
                role: form.role // ✅ send role as string
            };
            await api.post("/auth/register", body, {
                headers: { "Content-Type": "application/json" }
            });
            setMessage("Registration successful! Please login.");
        } catch (err) {
            setMessage(err.response?.data?.message || "Error registering");
        }
    };

    return (
        <div className="flex items-center justify-center h-screen bg-gray-50">
            <form
                onSubmit={handleSubmit}
                className="bg-white p-6 rounded shadow-md w-96"
            >
                <h2 className="text-xl font-bold mb-4">Register</h2>
                <input
                    name="fullName"
                    placeholder="Full Name"
                    value={form.fullName}
                    onChange={handleChange}
                    className="border p-2 w-full mb-2 rounded"
                />
                <input
                    name="email"
                    placeholder="Email"
                    type="email"
                    value={form.email}
                    onChange={handleChange}
                    className="border p-2 w-full mb-2 rounded"
                />
                <input
                    name="password"
                    placeholder="Password"
                    type="password"
                    value={form.password}
                    onChange={handleChange}
                    className="border p-2 w-full mb-2 rounded"
                />
                <select
                    name="role"
                    value={form.role}
                    onChange={handleChange}
                    className="border p-2 w-full mb-4 rounded"
                >
                    <option value="ROLE_STUDENT">Student</option>
                    <option value="ROLE_TEACHER">Teacher</option>
                    <option value="ROLE_ADMIN">Admin</option>
                </select>
                <button className="bg-blue-500 text-white p-2 w-full rounded">
                    Register
                </button>
                {message && (
                    <p className="mt-2 text-sm text-red-500">{message}</p>
                )}
            </form>
        </div>
    );
}
