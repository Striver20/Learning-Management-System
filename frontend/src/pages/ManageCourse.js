import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../utils/api";

export default function ManageCourse() {
    const { id } = useParams(); // courseId
    const [contents, setContents] = useState([]);
    const [newContent, setNewContent] = useState({
        title: "",
        description: "",
        contentType: "VIDEO",
        orderIndex: 1,
        file: null,
    });

    useEffect(() => {
        fetchContents();
    }, []);

    const fetchContents = async () => {
        try {
            const res = await api.get(`/contents?courseId=${id}`);
            setContents(res.data);
        } catch (err) {
            console.error("Error fetching contents", err);
        }
    };

    const handleFileChange = (e) => {
        setNewContent({ ...newContent, file: e.target.files[0] });
    };

    const handleAddContent = async () => {
        try {
            const formData = new FormData();
            formData.append("courseId", id);
            formData.append("title", newContent.title);
            formData.append("description", newContent.description);
            formData.append("contentType", newContent.contentType);
            formData.append("orderIndex", newContent.orderIndex);
            formData.append("file", newContent.file);

            await api.post("/contents/upload", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
            });

            setNewContent({ title: "", description: "", file: null, contentType: "VIDEO", orderIndex: 1 });
            fetchContents();
        } catch (err) {
            console.error("Error adding content", err);
        }
    };

    // ✅ Delete content
    const handleDeleteContent = async (contentId) => {
        if (!window.confirm("Are you sure you want to delete this content?")) return;

        try {
            await api.delete(`/contents/${contentId}`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
            });
            fetchContents(); // refresh list
        } catch (err) {
            console.error("Error deleting content", err);
        }
    };

    return (
        <div className="p-6">
            <h1 className="text-xl font-bold mb-4">Manage Course Contents</h1>

            {/* Add new content form */}
            <div className="p-4 border rounded mb-6">
                <h2 className="font-semibold mb-2">Add Content</h2>
                <input
                    type="text"
                    placeholder="Title"
                    className="border p-2 w-full mb-2"
                    value={newContent.title}
                    onChange={(e) => setNewContent({ ...newContent, title: e.target.value })}
                />
                <textarea
                    placeholder="Description"
                    className="border p-2 w-full mb-2"
                    value={newContent.description}
                    onChange={(e) => setNewContent({ ...newContent, description: e.target.value })}
                />
                <select
                    className="border p-2 w-full mb-2"
                    value={newContent.contentType}
                    onChange={(e) => setNewContent({ ...newContent, contentType: e.target.value })}
                >
                    <option value="VIDEO">Video</option>
                    <option value="PDF">PDF</option>
                    <option value="DOC">Document</option>
                </select>
                <input
                    type="number"
                    placeholder="Order Index"
                    className="border p-2 w-full mb-2"
                    value={newContent.orderIndex}
                    onChange={(e) => setNewContent({ ...newContent, orderIndex: e.target.value })}
                />
                <input
                    type="file"
                    className="border p-2 w-full mb-2"
                    onChange={handleFileChange}
                />
                <button
                    onClick={handleAddContent}
                    className="bg-green-600 text-white px-4 py-2 rounded"
                >
                    Upload Content
                </button>
            </div>

            {/* List contents */}
            <h2 className="text-lg font-semibold mb-2">Course Contents</h2>
            <ul>
                {contents.map((c) => (
                    <li key={c.id} className="p-4 border rounded mb-2 flex justify-between items-center">
                        <div>
                            <h3 className="font-bold">{c.title}</h3>
                            <p>{c.description}</p>
                            <span className="text-sm text-gray-500">{c.contentType}</span>
                            {c.fileUrl && (
                                <div className="mt-2">
                                    <a
                                        href={c.fileUrl}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        className="text-blue-600 underline"
                                    >
                                        View File
                                    </a>
                                </div>
                            )}
                        </div>
                        <button
                            onClick={() => handleDeleteContent(c.id)}
                            className="bg-red-600 text-white px-3 py-1 rounded hover:bg-red-700"
                        >
                            Delete
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );
}
