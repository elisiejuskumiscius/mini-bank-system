resource "aws_s3_bucket" "tf_state" {
  bucket = "elisiejus-app-tf-state-bucket"
  lifecycle {
    prevent_destroy = false
  }
}
resource "aws_dynamodb_table" "terraform_locks" {
  name           = "terraform-locks"
  billing_mode   = "PROVISIONED"
  read_capacity  = 5
  write_capacity = 5
  hash_key       = "LockID"

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = {
    Name = "TerraformLockTable"
  }
}