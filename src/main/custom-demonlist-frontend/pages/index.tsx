import React from "react";
import Layout from "@/layout/Layout";
import {extractTokenData} from "@/api/auth";

interface HomeProps {
    user: string;
}

const Home: React.FC<HomeProps> = ({user}) => {
    return (
        <Layout>
            <main>
            </main>
        </Layout>
    );
};

export async function getServerSideProps(context: any) {
    console.log(extractTokenData(context.req));

    return {
        props: {
            user: "0",
        },
    }
}

export default Home;
