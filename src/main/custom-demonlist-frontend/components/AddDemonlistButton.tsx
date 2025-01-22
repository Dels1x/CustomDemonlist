import Image from "next/image";
import React from "react";

export default function AddDemonlistButton() {
    return (
        <div>
            <button>
                <Image
                    src={"addplus.svg"}
                    alt={"Add"}
                    width={25}
                    height={25}/>
                New Demonlist
            </button>
        </div>
    )
}