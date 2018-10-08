#!/bin/env python
# coding: utf-8

from __future__ import print_function
import argparse
import re
import os
import unicodedata
import sys

import pandas as pd


def wiki_to_md(text):
    """Convert wiki formatting to markdown formatting.

    :param text: string of text to process

    :return: processed string
    """
    fn = []
    new_text = []
    fn_n = 1  # Counter for footnotes
    for line in text.split('\n'):
        # lists (unordered, ordered and mixed)
        match = re.match("^([#\*]+)(.*)", line)
        if match:
            list, text = match.groups()
            num_of_spaces = 4 * (len(list) - 1)
            spaces = " " * num_of_spaces
            list_type = ""
            if list[-1] == "#":
                list_type = "1."
            else:
                list_type = "*"
            additional_space = ""
            if text[0] != " ":
                additional_space = " "
            line = spaces + list_type + additional_space + text
        # Replace wiki headers with markdown headers
        match = re.match('(!+)(\\s?)[^\\[]', line)
        if match:
            header, spaces = match.groups()
            new_str = '#' * len(header)
            line = re.sub('(!+)(\\s?)([^\\[])', new_str + ' ' + '\\3', line)
        # Underline (doesn't exist in MD)
        line = re.sub("__(.*?)__", "\\1", line)
        # Bold
        line = re.sub("''(.*?)''", "**\\1**", line)
        # Italics
        line = re.sub("//(.*?)//", "_\\1_", line)
        # Remove wiki links
        line = re.sub("\\[\\[(\\w+?)\\]\\]", "\\1", line)
        # Change links to markdown format
        line = re.sub("\\[\\[(.*)\\|(.*)\\]\\]", "[\\1](\\2)", line)
        # Code
        line = re.sub("\\{\\{\\{(.*?)\\}\\}\\}", "`\\1`", line)
        # Footnotes
        match = re.search("```(.*)```", line)
        if match:
            text = match.groups()[0]
            fn.append(text)
            line = re.sub("```(.*)```", '[^{}]'.format(fn_n), line)
            fn_n += 1
        new_text.append(line)

    # Append footnotes
    for i, each in enumerate(fn):
        new_text.append('[^{}]: {}'.format(i+1, each))
    return '\n'.join(new_text)


def main(args):

    input_file = open(args.input_file)
    output_file = open(args.output_file, 'w')

    output_file.write(wiki_to_md(input_file.read()))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description="Convert TiddlyWiki tiddlers exported as CSV to "
                    "individual files.")
    parser.add_argument('input_file',
                        help='input file')
    parser.add_argument('output_file',
                        help='output file')
    args = parser.parse_args()

    sys.exit(main(args))
