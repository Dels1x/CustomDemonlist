import cookie from "cookie";
import jwt from 'jsonwebtoken'

export interface AuthTokenPayload {
    id: string;
    username: string,
    sub: string,
}

export const extractTokenData = (req: any): AuthTokenPayload | null => {
    if (!req.headers.cookie) return null;

    const cookies = cookie.parse(req.headers.cookie);
    const token = cookies['access-token'];

    if (!token) return null;

    try {
        return jwt.verify(token, process.env.JWT_SECRET_KEY || '') as AuthTokenPayload;
    } catch (err) {
        return null;
    }
};