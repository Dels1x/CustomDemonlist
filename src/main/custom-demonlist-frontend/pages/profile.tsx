import {useAuthContext} from "@/context/AuthContext";
import Layout from "@/layout/Layout";
import {extractFromAccessToken, getAccessTokenAndRefreshToken} from "@/api/auth";
import Test from "@/components/Test";

interface ProfileProps {

}

const Profile: React.FC<ProfileProps> =({}) => {
    const {user} = useAuthContext();
    console.log("USER: ", user);

    return (
        <Layout>
            Hello, #{user?.sub} - {user?.username}!
            <Test />
        </Layout>
    );
};

export async function getServerSideProps(context: any) {
    const accessToken = await getAccessTokenAndRefreshToken(context);
    const user = accessToken ? extractFromAccessToken(accessToken) : null;
    console.log("accessToken: ", JSON.stringify(accessToken));
    console.log("user: " + JSON.stringify(user));

    return {
        props: {
            user,
            accessToken,
        },
    }
}

export default Profile;
