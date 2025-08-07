import React, {useEffect} from "react";
import {deleteDemonlist, getDemonlist, updateDemonlistName} from "@/api/api";
import {
    extractFromAccessToken,
    getAccessTokenAndRefreshToken,
} from "@/api/auth";
import Layout from "@/layout/Layout";
import DemonlistManager from "@/components/DemonlistManager";
import {useOptionalDemonlistContext} from "@/context/DemonlistContext";
import DeleteButton from "@/components/DeleteButton";
import {useRouter} from "next/router";
import styles from "@/styles/Demonlist.module.css"

interface DemonlistProps {
    demonlist: any;
    user: any;
    accessToken: any;
}

const DemonlistPage: React.FC<DemonlistProps> = ({demonlist, accessToken, user}) => {
    const demonlistContext = useOptionalDemonlistContext();
    const refreshDemonlists = demonlistContext?.refreshDemonlists || (() => Promise.resolve());

    const [isEditing, setEditing] = React.useState(false);
    const isEditable = user ? user.sub === String(demonlist.person.id) : false;
    const [name, setName] = React.useState(demonlist.name);
    const router = useRouter();

    console.log("=== DemonlistPage Debug ===");
    console.log("demonlist:", demonlist);
    console.log("accessToken:", accessToken);
    console.log("user:", user);
    console.log("========================");


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
        if (isEditable) {
            await deleteDemonlist(demonlist.id, accessToken);
            refreshDemonlists();
            router.push("/");
        }
    }

    return (
        <Layout>
            <main>
                <div className={styles.demonlist}>
                    {isEditing ? (
                        <input
                            type="text"
                            autoFocus
                            value={name}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            onKeyDown={handleKeyDown}
                        />
                    ) : (
                        <span onDoubleClick={isEditable ? doubleClick : undefined}>
                        {`#${demonlist.id} - ${name}`}
                    </span>
                    )}

                    {isEditable && (
                        <DeleteButton
                            onDelete={handleDeleteDemonlist}
                            label={`Delete ${name}`}
                        />
                    )}

                    <DemonlistManager accessToken={accessToken} demonlist={demonlist} isEditable={isEditable}/>
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

    // Handle case where demonlist is null (error occurred or not found)
    if (!demonlist) {
        return {
            notFound: true,
        };
    }

    return {
        props: {
            demonlist,
            user,
            // Ensure accessToken is null instead of undefined for JSON serialization
            accessToken: accessToken || null,
        }
    }
}

export default DemonlistPage;
