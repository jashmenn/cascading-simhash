(ns simhash.core)


(comment

  we're writing a public function simHash

  it will take a
  Tap source
  Tap output
  Fields wanted
  a tokenizer
  int restrictiveness

  what its going to do is
  read the tap
  for each record
  tokenize the body
  convert it to a set 
    iterate over the tokens (stream it?)
    create a PriorityQueue and only store the lowest r hashes
  XOR the hashes
  return the tuple

  public Flow simhashTap source, Tap output, Fields wantedFields, Iterator<String>, int restrictiveness 
  )
