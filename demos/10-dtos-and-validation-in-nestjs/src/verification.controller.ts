import { Body, Controller, Post } from "@nestjs/common";
import { VerificationService } from "./verification.service";
import { VerifyCredentialsDto } from "./verify-credentials.dto";

@Controller("verify")
export class VerificationController {
  constructor(private readonly verificationService: VerificationService) {}

  @Post()
  checkCredentials(@Body() body: VerifyCredentialsDto) {
    // By the time this line runs, body has ALREADY been validated against
    // VerifyCredentialsDto's decorators - if it failed, this line never
    // executes at all.
    const verified = this.verificationService.verify(body.username);
    if (body.newUser) {
      return { username: body.username, verified, newUser: true };
    }
    return { username: body.username, verified };
  }
}
