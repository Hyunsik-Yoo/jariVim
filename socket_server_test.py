# -*- coding: utf-8 -*-

import socket
import sqlite3
import datetime

def listening_message():
    sqlite_conn = sqlite3.connect('market.db')

    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(('', 50007))
    s.listen(2)

    while 1:
        (conn, address) = s.accept()
        print 'Connected by ', address
        command = conn.recv(1024)
        if not command:
            break
        elif command == 'get_market_list_bob':
            get_voting_list(sqlite_conn, conn, '밥')

        elif command == 'get_market_list_noddle':
            get_voting_list(sqlite_conn, conn, '면')

        elif command == 'get_market_list_fastfood':
            get_voting_list(sqlite_conn, conn, '분식')

        elif command == 'get_market_list_fork':
            get_voting_list(sqlite_conn, conn, '고기')

        elif command == 'get_market_list_cafe':
            get_voting_list(sqlite_conn, conn, '커피')

        elif command == 'get_market_list_pup':
            get_voting_list(sqlite_conn, conn, '술')

        elif command == 'admin':
            return 1

        else:
            print 'error : ', command
        conn.close()


def admin_page(sqlite_conn, conn):
    conn.send("success")

    command = conn.recv(1024)
    command = command.split(',')

    if not command[0]:
        conn.send("command was null")

    elif command[0] == "insert_market":
        insert_market(sqlite_conn, command[1], command[2])

    elif command[0] == "delete_market":
        delete_market(sqlite_conn, command[1])







def create_table():
    sqlite_conn = sqlite3.connect('market.db')
    sqlite_conn.text_factory = str #한글 이름 삽입 가능하게 해주는 것
    c = sqlite_conn.cursor()

    #가게 이름들이 저장된 테이블, 카테고리도 같이 저장
    #c.execute("CREATE TABLE market_list (name text, category text)")

    #사람들이 투표한것들이 저장되는 디비, 현재 비율계산할때 사용된다.
    c.execute("CREATE TABLE voting_list (timestamp timestamp, name text, category text, percentage integer)")
    #c.execute("DROP TABLE voting_list") #테이블 삭제 명령

    sqlite_conn.commit()  #변경된 내용 저장
    sqlite_conn.close()


# 가게 이름을 market_list에 추가하는 함수
def insert_market(sqlite_conn, market_name, market_category):
    c = sqlite_conn.cursor()
    c.execute("INSERT INTO market_list VALUES(?,?)", (market_name,market_category))
    sqlite_conn.commit()


#투표결과를 저장해주는 함수
def insert_voting(sqlite_conn, market_name, value):
    c = sqlite_conn.cursor()

    c.execute("SELECT * FROM market_list WHERE name= '%s'" %market_name)
    category = c.fetchone()[1]
    now = datetime.datetime.now()
    c.execute("INSERT INTO voting_list VALUES(?,?,?,?)", (now, market_name, category, value))
    sqlite_conn.commit()


#가게 지울때 사용
def delete_market(sqlite_conn, market_name):
    c = sqlite_conn.cursor()
    c.execute("DELETE FROM market_list WHERE name=(?)", (market_name))
    c.execute("DELETE FROM voting_list WHERE name=(?)", (market_name))
    sqlite_conn.commit()


#가게이름:비율,가게이름2:비율 형식으로 전송
def get_voting_list(sqlite_conn, conn, category):
    c = sqlite_conn.cursor()
    query = c.execute("SELECT * FROM market_list WHERE category= '%s'" % category)
    market_list = []
    result = []
    for row in query:
        market_list.append(row[0])

    for row in market_list:
        query_second = c.execute("SELECT * FROM voting_list WHERE name= '%s' ORDER BY timestamp DESC LIMIT 5" % row)
        population_list = []
        for row2 in query_second:
            population_list.append(row2[3])
        if len(population_list) == 0:
            avg_population = 'null'
        else:
            avg_population = sum(population_list)/len(population_list)

        result.append(row+":"+str(avg_population))

    result = ','.join(result)
    conn.send(result.encode('utf-8'))



#sqlite_conn = sqlite3.connect('market.db')
#sqlite_conn.text_factory = str #한글 이름 삽입 가능하게 해주는 것
#insert_voting(sqlite_conn, "봉구스 밥버거", 50)
#get_voting_list(sqlite_conn, 0, '밥')
#get_market_list_by_category(sqlite_conn, 0, '밥')
listening_message()
#create_table()