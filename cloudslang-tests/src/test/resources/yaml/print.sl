#   (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
#   All rights reserved. This program and the accompanying materials
#   are made available under the terms of the Apache License v2.0 which accompany this distribution.
#
#   The Apache License is available at
#   http://www.apache.org/licenses/LICENSE-2.0

namespace: user.ops

operation:
  name: print
  inputs:
    - text
  action:
    python_script: |
      returnResult="Wow man"
      print text
  outputs:
    - returnResult
    - printed_text : ${text}
  results:
    - SUCCESS