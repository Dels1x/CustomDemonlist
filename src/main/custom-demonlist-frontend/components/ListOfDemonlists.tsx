import React from "react";
import Link from "next/link";
import {useDemonlistContext} from "@/context/DemonlistContext";

const ListOfDemonlists = ()=> {
    const {demonlists} = useDemonlistContext();
    console.log("ListOfDemonlists: ", demonlists);
    if (!demonlists || demonlists.length === 0) {
        return <div>No demonlists yet.</div>;
    }

    return (
        <div>
            {demonlists.map((item, index) => (
                    <Link href={`/demonlists/${item.id}`}>
                        <div key={index}>{item.name}</div>
                    </Link>
            ))}
        </div>
    );
}

export default ListOfDemonlists;
