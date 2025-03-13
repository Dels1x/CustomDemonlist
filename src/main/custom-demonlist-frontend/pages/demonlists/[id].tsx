import React from "react";
import {getDemonlist, updateDemonlistName} from "@/api/api";
import {
    extractFromAccessToken,
    getAccessTokenAndRefreshToken,
} from "@/api/auth";
import Layout from "@/layout/Layout";
import DemonlistManager from "@/components/DemonlistManager";

interface DemonlistProps {
    demonlist: any;
    user: any;
    accessToken: any;
}

const DemonlistPage: React.FC<DemonlistProps> = ({demonlist, user, accessToken}) => {
    const [isEditing, setEditing] = React.useState(false);
    const [name, setName] = React.useState(demonlist.name);

    const doubleClick = () => {
        setEditing(true);
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setName(e.target.value);
    }

    const handleBlur = async () => {
        await saveNameToDatabase();
        setEditing(false);
    }

    const handleKeyDown = async (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e && e.key !== "Enter") return;

        await saveNameToDatabase();
        setEditing(false);
    }

    const saveNameToDatabase = async () => {
        if (name !== demonlist.name) {
            await updateDemonlistName(demonlist.id, name, accessToken);
            demonlist.name = name;
        }
    }

    return (
        <Layout user={user} accessToken={accessToken}>
            <main>
                <div>
                    {
                        isEditing ?
                            (
                                <input
                                    type="text"
                                    autoFocus
                                    value={name}
                                    onChange={handleChange}
                                    onBlur={handleBlur}
                                    onKeyDown={(e) => handleKeyDown(e)}
                                />
                            )
                            :
                            (
                                <span onDoubleClick={doubleClick}>
                                    {`#${demonlist.id} - ${name}`}
                                </span>
                            )
                    }
                    <DemonlistManager accessToken={accessToken} demonlist={demonlist}/>
                </div>
            </main>
        </Layout>
    );
}

export async function getServerSideProps(context: any) {
    const accessToken = await getAccessTokenAndRefreshToken(context);
    const user = accessToken ? extractFromAccessToken(accessToken) : null;
    const id = context.params!.id;

    const demonlist = await getDemonlist(id, accessToken);
    console.log("Demonlist: " + JSON.stringify(demonlist));

    return {
        props: {
            demonlist,
            user,
            accessToken,
        }
    }

}

export default DemonlistPage;
