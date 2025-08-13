import axios from "axios";

const API_URL: string = "http://localhost:8080/";

export function getCookie(name: string, req: any): string {
    const cookies = req.headers.cookie;
    if (!cookies) {
        return '';
    }

    const match = cookies.match(new RegExp('(^|;\\s*)' + name + '=([^;]+)'));
    return match ? decodeURIComponent(match[2]) : '';
}

const api = axios.create({
    baseURL: API_URL,
});

export async function refreshToken(refreshToken: string) {
    try {
        const response = await api.post(
            "oauth2/refresh-access-token",
            null,
            {
                headers: {
                    "Refresh-Token": refreshToken,
                },
            });

        return response.data.accessToken;
    } catch (error) {
        console.error("Error refresh a token: ", error);
    }
}

export async function getDemonlistsForUserId(id: string, accessToken: string) {
    try {
        const response = await api.get(
            'demonlists/demonlists', {
                params: {userId: id},
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                }
            });

        return response.data;
    } catch (error) {
        console.error('Error fetching demonlists', error);
    }
}

export async function createNewDemonlist(demonlist: {
    name: string;
    isPublic: boolean;
    isMulti: boolean
}, accessToken: string) {
    try {
        const response = await api.post(
            'demonlists/create',
            demonlist,
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                },
            });

        return response.data;
    } catch (error) {
        console.error('Error adding demonlist', error);
    }
}

export async function countDemonsInDemonlist(id: number, accessToken: string) {
    try {
        const response = await api.get(
            'demons/count', {
                params: {demonlistId: id},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            });

        return response.data;
    } catch (error) {
        console.error('Error getting demons count', error);
    }
}

export async function countDemonlistsByUser(id: string, accessToken: string) {
    try {
        const response = await api.get(
            'demonlists/count', {
                params: {userId: id},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            });

        return response.data;
    } catch (error) {
        console.error('Error geting demonlists count', error)
    }
}

export async function getDemonlist(demonlistId: string, accessToken: string | null) {
    try {
        if (accessToken === null) {
            const response = await api.get(
                'demonlists/demonlist', {
                    params: {id: demonlistId}
                });

            return response.data;
        } else {
            const response = await api.get(
                'demonlists/demonlist', {
                    params: {id: demonlistId},
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                });

            return response.data;
        }
    } catch (error) {
        console.error(`Error getting demonlist by id ${demonlistId}`, error);
    }
}

export async function createNewDemon(
    demon: {
        name: string;
        author: string;
    },
    id: number,
    accessToken: string) {

    try {
        const response = await api.post(
            'demons/create',
            demon,
            {
                params: {demonlistId: id},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        );

        return response.data;
    } catch (error) {
        console.error('Error geting demonlist\'s count', error)
    }
}

export async function updateDemonlistName(id: number,
                                          name: string,
                                          accessToken: string) {
    try {
        const response = await api.patch(
            'demonlists/update-name',
            null,
            {
                params: {id: id, name: name},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        );

        return response.data;
    } catch (error) {
        console.error('Error updating demonlist\'s name', error);
    }
}

export async function updateDemonName(id: number,
                                      name: string,
                                      accessToken: string) {
    try {
        const response = await api.patch(
            'demons/update-name',
            null,
            {
                params: {id: id, name: name},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        )

        return response.data;
    } catch (error) {
        console.error('Error updating demon\'s name', error);
    }
}

export async function updateDemonAuthor(id: number,
                                        author: string,
                                        accessToken: string) {
    try {
        const response = await api.patch(
            'demons/update-author',
            null,
            {
                params: {id: id, author: author},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        )

        return response.data;
    } catch (error) {
        console.error('Error updating demon\'s author', error);
    }
}

export async function updateDemonAttempts(id: number,
                                          attempts: string,
                                          accessToken: string) {
    try {
        const response = await api.patch(
            'demons/update-attempts',
            null,
            {
                params: {id: id, attemptsCount: attempts},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        )

        return response.data;
    } catch (error) {
        console.error('Error updating demon\'s attempts count', error);
    }
}


export async function updateDemonEnjoyment(id: number,
                                           enjoyment: string,
                                           accessToken: string) {
    try {
        const response = await api.patch(
            'demons/update-enjoyment',
            null,
            {
                params: {id: id, enjoyment: enjoyment},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        )

        return response.data;
    } catch (error) {
        console.error('Error updating demon\'s enjoyment rating', error);
    }
}

export async function updateDemonPosition(id: number,
                                          position: number,
                                          accessToken: string) {
    try {
        const response = await api.patch(
            'demons/update-position',
            null,
            {
                params: {id: id, position: position},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        );

        return response.data;
    } catch (error) {
        console.error('Error updating position', error);
    }
}

export async function updateDemonDifficulty(id: number,
                                          difficulty: string,
                                          accessToken: string) {
    console.log("sending request to update difficulty to: ", difficulty);
    try {
        const response = await api.patch(
            'demons/update-difficulty',
            null,
            {
                params: {id: id, difficulty: difficulty},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        );

        return response.data;
    } catch (error) {
        console.error('Error updating position', error);
    }
}

export async function updateDemonDateOfCompletion(id: number,
                                                  date: string,
                                                  accessToken: string) {
    console.log("sending request to update date of completion to: ", date);
    try {
        const response = await api.patch(
            'demons/update-completion-date',
            null,
            {
                params: {id: id, date: date},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        );

        return response.data;
    } catch (error) {
        console.error('Error updating position', error);
    }
}

export async function updateDemonWorstFail(id: number,
                                                  worstFail: string,
                                                  accessToken: string) {
    console.log("sending request to update worst fail to: ", worstFail);
    try {
        const response = await api.patch(
            'demons/update-worst-fail',
            null,
            {
                params: {id: id, worstFail: worstFail},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        );

        return response.data;
    } catch (error) {
        console.error('Error updating position', error);
    }
}

export async function deleteDemonlist(id: number,
                                      accessToken: string) {
    try {
        const response = await api.delete(
            'demonlists/delete',
            {
                params: {id: id},
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        );

        return response.data;
    } catch (error) {
        console.error('Error deleting demonlist', error);
    }
}

export async function deleteDemon(demonId: number,
                                  accessToken: string) {
    try {
        const url = `demons/delete?demonId=${demonId}`;
        const response = await api.delete(
            url,
            {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            }
        );

        return response.data;
    } catch (error) {
        console.error('Error deleting demonlist', error);
    }
}

// USER endpoints

export async function getUser(id: number) {
    try {
        const url = `users/user?id=${id}`;
        const response = await api.get(url);

        return response.data;
    } catch (error) {
        console.error('Error deleting demonlist', error);
    }
}

export async function getUserStats(id: number) {
    try {
        const url = `users/stats?id=${id}`;
        const response = await api.get(url);

        return response.data;
    } catch (error) {
        console.error('Error deleting demonlist', error);
    }
}
