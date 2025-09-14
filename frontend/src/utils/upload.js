import api from "./api";

export async function uploadFile({ courseId, title, description, contentType, orderIndex, file }) {
    const formData = new FormData();
    formData.append("courseId", courseId);
    formData.append("title", title);
    formData.append("description", description);
    formData.append("contentType", contentType);
    formData.append("orderIndex", orderIndex);
    formData.append("file", file);

    const res = await api.post("/contents/upload", formData, {
        headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
    });

    return res.data; // should return the Content object with fileUrl
}
