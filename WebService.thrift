namespace java com.yuanwhy.service
service WebService{
    list<string> get(1:string user),
    void put(1:string user, 2:string friend)
    void removeuser(1:string user),
    void removefriend(1:string user, 2:string friend),
    list<string> getwithtime(1:string user, 2:i32 t),
    list<string> diff(1:string user, 2:i32 t1, 3:i32 t2) 
}

