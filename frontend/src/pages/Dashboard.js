import { useEffect, useState } from "react";
import api from "../utils/api";

export default function Dashboard() {
    const [courses, setCourses] = useState([]);

    useEffect(() => {
        api.get("/courses")
            .then(res => setCourses(res.data))
            .catch(err => console.error(err));
    }, []);

    return (
        <div className="p-6">
            <h1 className="text-2xl font-bold mb-4">Courses</h1>
            <ul>
                {courses.map(c => (
                    <li key={c.id} className="border p-3 mb-2 rounded shadow-sm">
                        <h2 className="font-semibold">{c.title}</h2>
                        <p>{c.description}</p>
                    </li>
                ))}
            </ul>
        </div>
    );
}
