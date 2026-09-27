import json
import re

transcript_path = r"C:\Users\Hiago\.gemini\antigravity\brain\dd24e648-9dd8-487b-9a86-72dd62896047\.system_generated\logs\transcript_full.jsonl"

with open(transcript_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

last_user_msg = ""
for line in reversed(lines):
    data = json.loads(line)
    if data.get('source') == 'USER_EXPLICIT':
        last_user_msg = data.get('content', '')
        break

pdf_pattern = r'^(\d+)\s+(\d+)\s+(.*?)\s+(\d+,\d+)\s+(\d+,\d+)\s+(\d+,\d+)\s+(\d+,\d+)\s+(\d+,\d+)\s+(\d+,\d+)$'
ipm_dict = {}

count = 0
for line in last_user_msg.split('\n'):
    line = line.strip()
    match = re.search(pdf_pattern, line)
    if match:
        count += 1
        name = match.group(3).strip()
        ipm = match.group(9)
        ipm_dict[name] = ipm
    elif "ABAIRA" in line:
        print("Failed to match ABAIRA line:", repr(line))

print("Matched:", count)
