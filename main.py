import mysql.connector
import json

config = {
    "host": "45.58.126.118",
    "user": "u22_xWhxaodt78",
    "password": "slTQzhzVMx25MrDPDs!h.A^4",
    "database": "s22_BeansLicenses"
}

conn = mysql.connector.connect(**config)
cursor = conn.cursor(dictionary=True)

dump = {}

cursor.execute("SHOW TABLES")
tables = [list(t.values())[0] for t in cursor.fetchall()]

for table in tables:
    cursor.execute(f"SELECT * FROM `{table}`")
    dump[table] = cursor.fetchall()

with open("dump_test.json", "w", encoding="utf-8") as f:
    json.dump(dump, f, indent=2, ensure_ascii=False)

cursor.close()
conn.close()

print("✅ Dump JSON generado")
