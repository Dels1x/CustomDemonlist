import React from "react";
import Link from "next/link";
import {Demonlist} from "@/lib/models";

interface ListOfDemonlistsProps {
    list: Demonlist[];
}

const ListOfDemonlists: React.FC<ListOfDemonlistsProps> = ({list})=> {
    if (!list || list.length === 0) {
        return <div>No demonlists yet.</div>;
    }

    return (
        <div>
            {list.map((item, index) => (
                    <Link href={`/demonlists/${item.id}`}>
                        <div key={index}>{item.name}</div>
                    </Link>
            ))}
        </div>
    );
}

export default ListOfDemonlists;
