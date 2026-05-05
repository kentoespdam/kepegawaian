CHUNK_NUM=$1
FILES=$(cat graphify-out/.graphify_chunk_list_${CHUNK_NUM}.txt)
# Subagent will run its own logic and write to graphify-out/.graphify_chunk_${CHUNK_NUM}.json
