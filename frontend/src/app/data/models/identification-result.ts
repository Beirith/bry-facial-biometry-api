import {User} from './user';

export interface IdentificationResult {
  identified: boolean;
  user: User | null;
  score: number;
}
