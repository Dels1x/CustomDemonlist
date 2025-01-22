import React from "react";

interface ListOfDemonlistsProps {
    list: ListItem[];
}

interface ListItem {
    name: string;
}

const ListOfDemonlists: React.FC<ListOfDemonlistsProps> = ({list})=> {

    return (
        <div>
            {list.map((item, index) => (
                    <div key={index}>{item.name}</div>
            ))}
        </div>
    );
}

export default ListOfDemonlists;
