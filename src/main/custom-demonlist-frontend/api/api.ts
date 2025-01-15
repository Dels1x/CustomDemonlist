import axios from "axios";

const API_URL: string = "http://localhost:8080/";

function getCookie(name: string): string | null {
    const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
    if (match) return decodeURIComponent(match[2]);
    return null;
}

const api = axios.create({
        baseURL: API_URL,
    });

/* TODO
api.interceptors.request.use(async (config) => {
    let token = localStorage.getItem('access-token');
})*/
