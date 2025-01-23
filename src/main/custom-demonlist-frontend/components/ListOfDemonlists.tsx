import React from "react";

interface ListOfDemonlistsProps {
    list: ListItem[];
}

export interface ListItem {
    name: string;
}

const ListOfDemonlists: React.FC<ListOfDemonlistsProps> = ({list})=> {
    if (!list || list.length === 0) {
        return <div>No demonlists yet.</div>;
    }

    return (
        <div>
            {list.map((item, index) => (
                    <div key={index}>{item.name}</div>
            ))}
        </div>
    );
}

export default ListOfDemonlists;
