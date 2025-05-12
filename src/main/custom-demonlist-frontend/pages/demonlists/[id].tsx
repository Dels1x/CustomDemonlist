import React, {useEffect} from "react";
import {deleteDemonlist, getDemonlist, updateDemonlistName} from "@/api/api";
import {
    extractFromAccessToken,
    getAccessTokenAndRefreshToken,
} from "@/api/auth";
import Layout from "@/layout/Layout";
import DemonlistManager from "@/components/DemonlistManager";
import {useDemonlistContext} from "@/context/DemonlistContext";
import DeleteButton from "@/components/DeleteButton";
import {useRouter} from "next/router";

interface DemonlistProps {
    demonlist: any;
    user: any;
    accessToken: any;
}

const DemonlistPage: React.FC<DemonlistProps> = ({demonlist, accessToken}) => {
    const {refreshDemonlists} = useDemonlistContext();
    const [isEditing, setEditing] = React.useState(false);
    const [name, setName] = React.useState(demonlist.name);
    const router = useRouter();

    useEffect(() => {
        setName(demonlist.name);
        setEditing(false);
    }, [demonlist]);

    const doubleClick = () => {
        setEditing(true);
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setName(e.target.value);
    }

    const handleBlur = async () => {
        await saveNameToDatabase();
        setEditing(false);
        refreshDemonlists();
    }

    const handleKeyDown = async (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e && e.key !== "Enter") return;

        await saveNameToDatabase();
        setEditing(false);
        refreshDemonlists();
    }

    const saveNameToDatabase = async () => {
        if (name !== demonlist.name) {
            await updateDemonlistName(demonlist.id, name, accessToken);
            demonlist.name = name;
        }
    }

    const handleDeleteDemonlist = async () => {
        await deleteDemonlist(demonlist.id, accessToken);
        refreshDemonlists();
        router.push("/");
    }

    return (
        <Layout>
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
                    <DeleteButton
                        onDelete={handleDeleteDemonlist}
                        label={`Delete ${name}`}/>
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
