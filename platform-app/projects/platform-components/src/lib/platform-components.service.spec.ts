import {TestBed} from '@angular/core/testing';

import {PlatformComponentsService} from './platform-components.service';

describe('PlatformComponentsService', () => {
  let service: PlatformComponentsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlatformComponentsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
