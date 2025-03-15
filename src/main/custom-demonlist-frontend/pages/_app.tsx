import "@/styles/globals.css";
import type { AppProps } from "next/app";
import {AuthProvider} from "@/context/AuthContext";

export default function App({ Component, pageProps }: AppProps) {
  return (
      <AuthProvider accessToken={pageProps.accessToken} user={pageProps.user} >
        <Component {...pageProps} />
      </AuthProvider>
  );
}
