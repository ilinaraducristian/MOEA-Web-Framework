#!/usr/bin/env python

import yaml
import os
import re
import sys

API_FILE = sys.argv[1]
SPEC_FILE = sys.argv[2]
API_PATH = re.sub(r'/(.(?!/))+$', "", API_FILE)

with open(API_FILE, 'r') as stream:
    try:
        spec = yaml.safe_load(stream)
    except yaml.YAMLError as exc:
        print(exc)
        exit(-1)


def processFolder(name):
    try:
        array = os.listdir(f"{API_PATH}/{name}")
    except FileNotFoundError:
        return
    if len(array) == 0:
        return
    if spec["components"].get(name) is None:
        spec["components"][name] = {}
    for element in array:
        file = open(f"{API_PATH}/{name}/{element}", "r").read()
        element = element.replace(".yml", "")
        element = element.replace(".yaml", "")
        file = file.replace("../", "#/components/")
        file = file.replace("./", f"#/components/{name}/")
        file = file.replace(".yml", "")
        file = file.replace(".yaml", "")
        spec["components"][name][element] = yaml.safe_load(file)


def processPaths():
    try:
        paths = os.listdir(f"{API_PATH}/paths")
    except FileNotFoundError:
        return
    if len(paths) == 0:
        return

    path_objects = {}

    for path in paths:
        file = open(f"{API_PATH}/paths/{path}", "r").read()
        file = file.replace("../", "#/components/")
        file = file.replace(".yml", "")
        file = file.replace(".yaml", "")
        path_objects[path] = yaml.safe_load(file)

    for path in spec["paths"]:
        ref = spec["paths"][path]["$ref"]
        file_name = re.sub(r'.+/', "", ref)
        spec["paths"][path] = path_objects[file_name]


if spec.get("components") is None:
    spec["components"] = {}

processFolder("parameters")
processFolder("requestBodies")
processFolder("responses")
processFolder("schemas")
processPaths()

open(SPEC_FILE, "w").write(yaml.dump(spec, sort_keys=False))