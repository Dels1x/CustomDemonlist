import React from "react";
import Link from "next/link";

interface ListOfDemonlistsProps {
    list: ListItem[];
}

export interface ListItem {
    name: string;
    id: number;
}

const ListOfDemonlists: React.FC<ListOfDemonlistsProps> = ({list})=> {
    if (!list || list.length === 0) {
        return <div>No demonlists yet.</div>;
    }

    console.log("id: " + list[0].id);

    return (
        <div>
            {list.map((item, index) => (
                    <Link href={`/demonlists/${item.id}`}>
                        <div key={index}>#{item.id} - {item.name}</div>
                    </Link>
            ))}
        </div>
    );
}

export default ListOfDemonlists;
