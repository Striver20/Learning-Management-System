import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../utils/api";

export default function CoursePage() {
    const { id } = useParams(); // <-- student courseId
    const [contents, setContents] = useState([]);

    useEffect(() => {
        if (id) fetchContents();
    }, [id]);

    const fetchContents = async () => {
        try {
            const res = await api.get(`/contents?courseId=${id}`, {
                headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
            });
            setContents(res.data);
        } catch (err) {
            console.error("Error fetching course contents", err);
        }
    };

    return (
        <div className="p-6">
            <h1 className="text-xl font-bold mb-4">Course Contents</h1>
            {contents.length > 0 ? (
                <ul>
                    {contents.map(c => (
                        <li key={c.id} className="p-4 border rounded mb-2">
                            <h3 className="font-bold">{c.title}</h3>
                            <p>{c.description}</p>
                            {c.fileUrl && (
                                <a
                                    href={c.fileUrl}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="text-blue-600 underline"
                                >
                                    View File
                                </a>
                            )}
                        </li>
                    ))}
                </ul>
            ) : (
                <p className="text-gray-600">No contents available yet.</p>
            )}
        </div>
    );
}
