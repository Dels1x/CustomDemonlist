import axios from "axios";

const API_URL: string = "http://localhost:8080/";

export function getCookie(name: string, req: any): string {
    const cookies = req.headers.cookie;
    if (!cookies) {
        return '';
    }

    const match = cookies.match(new RegExp('(^|;\\s*)' + name + '=([^;]+)'));
    return match ? decodeURIComponent(match[2]) : '';
}

const api = axios.create({
        baseURL: API_URL,
    });

export async function refreshToken(refreshToken: string) {
    try {
        const response = await api.post(
            "oauth2/refresh-access-token",
            null,
            {
                headers: {
                    "Refresh-Token": refreshToken,
                },
            });

        console.log(response);
        const newAccessToken = response.data.accessToken;
        console.log(newAccessToken)
        return newAccessToken;
    } catch (error) {
        console.error("Error refresh a token: ", error);
        throw error;
    }
}