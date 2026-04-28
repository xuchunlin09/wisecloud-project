## Introduction
Since the message transmission distinguishes whether to encrypt or not, the signing rules for encrypted and non-encrypted messages are different. See below for details.

- For non-encrypted messages, when signing, the plaintext message needs to be sorted in ASCII code from small to large, then concatenated into a URL address string, and then signed;
- For encrypted messages, signing is performed directly on the encrypted message string.

The data messages transmitted in the entire system architecture are in JSON format. Before the data messages are transmitted, they are signed using MD5 or HmacSHA256 encryption technology. When MD5 or HmacSHA256 signing is performed, the primary accessKeySecret key is required to participate in the signing

## **Non-encrypted Message Signature Process**

### Step 1: **Determine the parameters to be signed**

For all API request parameter message nodes data in the client request parameter list, signing is required.

### Step 2: **Sort parameters**

Sort the parameter names in ASCII code from small to large (in alphabetical order from a to z; if the same first letter is encountered, look at the second letter, and so on).

### Step 3: Parameter stitching

Use the "&" character to connect the sorted parameters.

### Step 4: Splice the key

If the signature algorithm is MD5, concatenate the string obtained in Step 3 with accessKeySecret sign it with MD5.
If the signature algorithm is Hmac-SHA356, the string obtained in Step 3 and accessKeySecret are signed.

## Encrypted Message Signature

Client side and server level unified agreement AES encryption and decryption, the key is accessKeySecret.

## sdk test code 