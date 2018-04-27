import random

n_regions = 30
n_employees = 200
n_assets = 800
n_gas = 30
n_location = 30
n_vehicles = 30

n_readings_loc = 1
n_readings_gas = 1

regions = list()
region_query = 'INSERT INTO tbl_regions (region_id, type) VALUES ({}, {});'
region_types = 6

output = open('instances.sql', 'w')

for i in range(1, n_regions + 1):
    region = dict()
    region['region_id'] = i
    region['type'] = random.randint(1, region_types)
    regions.append(region)
    output.write(region_query.format(region['region_id'], region['type']) + "\n")


tags = 1

employees = list()
employee_query = 'INSERT INTO tbl_employees (employee_id, tag_id, auth_level, role) VALUES ({}, {}, {}, {});'
employee_levels = 5
employee_roles = 6

for i in range(1, n_employees + 1):
    emp = dict()
    emp['employee_id'] = i
    emp['tag_id'] = tags
    emp['auth_level'] = random.randint(1, employee_levels)
    emp['role'] = random.randint(1, employee_roles)
    employees.append(emp)
    
    tags += 1

    output.write(employee_query.format(emp['employee_id'], emp['tag_id'], emp['auth_level'], emp['role']) + "\n")


assets = list()
asset_query = 'INSERT INTO tbl_assets (asset_id, tag_id, type, assigned_region) VALUES ({}, {}, {}, {});'
asset_types = 7

for i in range(1, n_assets):
    ass = dict()
    ass['asset_id'] = i
    ass['tag_id'] = tags
    ass['type'] = random.randint(1, asset_types)
    ass['assigned_region'] = random.randint(1, n_regions)
    assets.append(ass)
    
    tags += 1

    output.write(asset_query.format(ass['asset_id'], ass['tag_id'], ass['type'], ass['assigned_region']) + "\n")


vehicles = list()
vehicle_query = 'INSERT INTO tbl_vehicles (vehicle_id, tag_id, type) VALUES ({}, {}, {});'
vehicle_types = 5

for i in range(1, n_vehicles + 1):
    veh = dict()
    veh['vehicle_id'] = i
    veh['tag_id'] = tags
    veh['type'] = random.randint(1, vehicle_types)
    vehicles.append(veh)
    
    tags += 1

    output.write(vehicle_query.format(veh['vehicle_id'], veh['tag_id'], veh['type']) + "\n")

gas_readers = list()
reader_query = 'INSERT INTO tbl_readers (rn_id, subregion_id, type, battery) VALUES ({}, {}, {}, {});'
reader_region = 0
for i in range(1, n_gas):
    gas = dict()
    gas['rn_id'] = i
    gas['subregion_id'] = (reader_region % (n_regions)) + 1
    gas['type'] = 1
    gas['battery'] = random.randint(0, 100)
    gas_readers.append(gas)

    output.write(reader_query.format(gas['rn_id'], gas['subregion_id'], gas['type'], gas['battery']) + "\n")
    reader_region += 1

reader_region = 0
location_readers = list()
for i in range(n_gas, n_gas + n_location):
    loc = dict()
    loc['rn_id'] = i
    loc['subregion_id'] = (reader_region % (n_regions)) + 1
    loc['type'] = 2
    loc['battery'] = random.randint(0, 100)
    location_readers.append(loc)

    output.write(reader_query.format(loc['rn_id'], loc['subregion_id'], loc['type'], loc['battery']) + "\n")
    reader_region += 1

output.close()

import datetime
import pymongo
from pymongo import MongoClient

client = MongoClient("mongodb://iot:iot@127.0.0.1:27017/smart_mine")
db = client['smart_mine']

for collection in db.collection_names():
    db.drop_collection(collection)

locations = db.locations
date = datetime.datetime(2018,2,1,12,4,5)
for i in range(n_readings_loc):
    for e in employees:
        reader = location_readers[random.randint(0, n_location - 1)]
        loc = dict()
        loc["tag_id"] = e["tag_id"]
        loc["motion_alarm"] = random.random() < 0.0001
        loc["panic"] = random.random() < 0.000001
        loc["rn_id"] = reader["rn_id"]
        loc["subregion_id"] = reader["subregion_id"]
        loc["create_date"] = date

        locations.insert(loc)

    for a in assets:
        reader = location_readers[random.randint(0, n_location - 1)]
        loc = dict()
        loc["tag_id"] = a["tag_id"]
        loc["motion_alarm"] = False
        loc["panic"] = False
        loc["rn_id"] = reader["rn_id"]
        loc["subregion_id"] = reader["subregion_id"]
        loc["create_date"] = date

        locations.insert(loc)

    date += datetime.timedelta(seconds=1)

result = locations.create_index([('tag_id', pymongo.ASCENDING)], unique=False)

#db.command({
#    "create": "tbl_last_locations",
#    "viewOn": "locations",
#    "pipeline": [{"$sort": {"tag_id": 1, "create_date": 1}},  {"$group": {"_id": "$tag_id", "lastReading": {"$last": "$create_date"}}}]
#    })

gas_levels = db.gas_levels
date = datetime.datetime(2018,2,1,12,4,5)
for i in range(n_readings_gas):
    for reader in gas_readers:
        gas = dict()
        gas["rn_id"] = reader["rn_id"]
        gas["oxygen"] = random.uniform(15.5, 22.0)
        gas["carbonmonoxide"] = random.uniform(0.0, 13.0)
        gas["methane"] = random.uniform(0.0, 6.0)
        gas["humidity"] = random.uniform(29.0, 70.0)
        gas["temperature"] = random.uniform(23.0, 30.0)
        gas["create_date"] = date

        gas_levels.insert(gas)

    date += datetime.timedelta(seconds=1)

result = gas_levels.create_index([('rn_id', pymongo.ASCENDING)], unique=False)