interface Demon {
    id: number;
    name: string;
    difficulty: string;
    placement: number;
    initial_placement: number;
}

interface DemonlistProps {
    demonlist: Demon[];
    id: number;
    person_id: number;
    is_public: boolean;
    is_multi: boolean;
}

const DemonlistPage: React.FC<DemonlistProps> = ({demonlist, id, person_id, is_public, is_multi}) => {
    return (
        <div>

        </div>
    );
}