import socket
import sqlite3



def listening_message():
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    s.bind(('', 50007))
    s.listen(2)

    (conn, address) = s.accept()

    print 'Connected by ', address

    while 1:
        message = conn.recv(1024)
        if not message:
            break

        elif message == 'get_market_list_bob':
            print message

        elif message == 'get_market_list_noddle':
            print messgae

        elif message == 'get_market_list_fastfood':
            print message

        elif message == 'get_market_list_fork':
            print message

        elif message == 'get_market_list_cafe':
            print message

        elif message == 'get_market_list_pup':
            print message

        else:
            print 'error : ', message
        conn.send(message)
    conn.close()


listening_message()
