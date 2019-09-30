import os
import argparse


def get_args():
    parser = argparse.ArgumentParser(description='sasdf')
    parser.add_argument('person', nargs='?', help='eeee')
    return parser.parse_args()


if __name__ == '__main__':
    args = get_args()
    if args.person == None:
        args.person = 'world'
    print(f'hello, {args.person}!')