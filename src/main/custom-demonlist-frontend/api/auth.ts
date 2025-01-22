import cookie from "cookie";
import jwt from 'jsonwebtoken'
import {getCookie, refreshToken} from "@/api/api";

export interface AuthTokenPayload {
    id: string;
    username: string,
    sub: string,
}

export const getAccessToken = (req: any): string | null => {
    if (!req.headers.cookie) return null;

    const cookies = cookie.parse(req.headers.cookie);
    const token = cookies['access-token'];
    return token === undefined ? null : token;
}

export const extractAccessTokenData = (req: any): AuthTokenPayload | null => {
    if (!req.headers.cookie) return null;

    const cookies = cookie.parse(req.headers.cookie);
    const token = cookies['access-token'];

    return extractFromAccessToken(token);
};

const extractFromAccessToken = (token: string | undefined): AuthTokenPayload | null => {
    if (!token) return null;

    try {
        return jwt.verify(token, process.env.JWT_SECRET_KEY || '') as AuthTokenPayload;
    } catch (err) {
        return null;
    }
};

export const getUserAndRefreshToken = async (context: any) => {
    let user = extractAccessTokenData(context.req);

    if (!user) {
        let token = getCookie("refresh-token", context.req);
        console.info("token: ", token);

        if (token != '') {
            let accessToken = await refreshToken(token);
            context.res.setHeader('Set-Cookie', `access-token=${accessToken}; HttpOnly; Path=/; Max-Age=3600; Secure`);
            user = extractFromAccessToken(accessToken);
        }
    }

    return user;
};
